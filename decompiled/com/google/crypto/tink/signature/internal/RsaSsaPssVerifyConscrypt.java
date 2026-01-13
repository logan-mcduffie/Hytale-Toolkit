package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.RsaSsaPssParameters;
import com.google.crypto.tink.signature.RsaSsaPssPublicKey;
import com.google.crypto.tink.subtle.Validators;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import javax.annotation.Nullable;

@Immutable
public final class RsaSsaPssVerifyConscrypt implements PublicKeyVerify {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private static final byte[] EMPTY = new byte[0];
   private static final byte[] legacyMessageSuffix = new byte[]{0};
   private static final String MGF_1 = "MGF1";
   private static final int TRAILER_FIELD_BC = 1;
   private final RSAPublicKey publicKey;
   private final String signatureAlgorithm;
   private final PSSParameterSpec parameterSpec;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;
   private final Provider conscrypt;

   @Nullable
   static Provider conscryptProviderOrNull() {
      return Util.isAndroid() && Util.getAndroidApiLevel() <= 23 ? null : ConscryptUtil.providerOrNull();
   }

   static String getConscryptRsaSsaPssAlgo(RsaSsaPssParameters.HashType hash) {
      if (hash == RsaSsaPssParameters.HashType.SHA256) {
         return "SHA256withRSA/PSS";
      } else if (hash == RsaSsaPssParameters.HashType.SHA384) {
         return "SHA384withRSA/PSS";
      } else if (hash == RsaSsaPssParameters.HashType.SHA512) {
         return "SHA512withRSA/PSS";
      } else {
         throw new IllegalArgumentException("Unsupported hash: " + hash);
      }
   }

   private static String getMdName(RsaSsaPssParameters.HashType sigHash) {
      if (sigHash == RsaSsaPssParameters.HashType.SHA256) {
         return "SHA-256";
      } else if (sigHash == RsaSsaPssParameters.HashType.SHA384) {
         return "SHA-384";
      } else if (sigHash == RsaSsaPssParameters.HashType.SHA512) {
         return "SHA-512";
      } else {
         throw new IllegalArgumentException("Unsupported MD hash: " + sigHash);
      }
   }

   private static MGF1ParameterSpec getMgf1Hash(RsaSsaPssParameters.HashType mgf1Hash) {
      if (mgf1Hash == RsaSsaPssParameters.HashType.SHA256) {
         return MGF1ParameterSpec.SHA256;
      } else if (mgf1Hash == RsaSsaPssParameters.HashType.SHA384) {
         return MGF1ParameterSpec.SHA384;
      } else if (mgf1Hash == RsaSsaPssParameters.HashType.SHA512) {
         return MGF1ParameterSpec.SHA512;
      } else {
         throw new IllegalArgumentException("Unsupported MGF1 hash: " + mgf1Hash);
      }
   }

   static PSSParameterSpec getPssParameterSpec(RsaSsaPssParameters.HashType sigHash, RsaSsaPssParameters.HashType mgf1Hash, int saltLength) {
      return new PSSParameterSpec(getMdName(sigHash), "MGF1", getMgf1Hash(mgf1Hash), saltLength, 1);
   }

   private RsaSsaPssVerifyConscrypt(
      final RSAPublicKey pubKey,
      RsaSsaPssParameters.HashType sigHash,
      RsaSsaPssParameters.HashType mgf1Hash,
      int saltLength,
      byte[] outputPrefix,
      byte[] messageSuffix,
      Provider conscrypt
   ) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Cannot use RSA SSA PSS in FIPS-mode, as BoringCrypto module is not available.");
      } else if (!sigHash.equals(mgf1Hash)) {
         throw new GeneralSecurityException("sigHash and mgf1Hash must be the same");
      } else {
         Validators.validateRsaModulusSize(pubKey.getModulus().bitLength());
         Validators.validateRsaPublicExponent(pubKey.getPublicExponent());
         this.publicKey = pubKey;
         this.signatureAlgorithm = getConscryptRsaSsaPssAlgo(sigHash);
         this.parameterSpec = getPssParameterSpec(sigHash, mgf1Hash, saltLength);
         this.outputPrefix = outputPrefix;
         this.messageSuffix = messageSuffix;
         this.conscrypt = conscrypt;
      }
   }

   public static PublicKeyVerify create(RsaSsaPssPublicKey key) throws GeneralSecurityException {
      Provider conscrypt = conscryptProviderOrNull();
      return createWithProvider(key, conscrypt);
   }

   @AccessesPartialKey
   public static PublicKeyVerify createWithProvider(RsaSsaPssPublicKey key, Provider conscrypt) throws GeneralSecurityException {
      if (conscrypt == null) {
         throw new NoSuchProviderException("RSA SSA PSS using Conscrypt is not supported.");
      } else {
         KeyFactory keyFactory = KeyFactory.getInstance("RSA", conscrypt);
         RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(new RSAPublicKeySpec(key.getModulus(), key.getParameters().getPublicExponent()));
         RsaSsaPssParameters params = key.getParameters();
         return new RsaSsaPssVerifyConscrypt(
            publicKey,
            params.getSigHashType(),
            params.getMgf1HashType(),
            params.getSaltLengthBytes(),
            key.getOutputPrefix().toByteArray(),
            key.getParameters().getVariant().equals(RsaSsaPssParameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY,
            conscrypt
         );
      }
   }

   @Override
   public void verify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
      if (!Util.isPrefix(this.outputPrefix, signature)) {
         throw new GeneralSecurityException("Invalid signature (output prefix mismatch)");
      } else {
         Signature verifier = Signature.getInstance(this.signatureAlgorithm, this.conscrypt);
         verifier.initVerify(this.publicKey);
         verifier.setParameter(this.parameterSpec);
         verifier.update(data);
         if (this.messageSuffix.length > 0) {
            verifier.update(this.messageSuffix);
         }

         if (!verifier.verify(signature, this.outputPrefix.length, signature.length - this.outputPrefix.length)) {
            throw new GeneralSecurityException("signature verification failed");
         }
      }
   }
}
