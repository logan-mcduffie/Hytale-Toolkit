package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.RsaSsaPkcs1Parameters;
import com.google.crypto.tink.signature.RsaSsaPkcs1PublicKey;
import com.google.crypto.tink.subtle.Validators;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import javax.annotation.Nullable;

@Immutable
public final class RsaSsaPkcs1VerifyConscrypt implements PublicKeyVerify {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private static final byte[] EMPTY = new byte[0];
   private static final byte[] legacyMessageSuffix = new byte[]{0};
   private final RSAPublicKey publicKey;
   private final String signatureAlgorithm;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;
   private final Provider conscrypt;

   @Nullable
   static Provider conscryptProviderOrNull() {
      return Util.isAndroid() && Util.getAndroidApiLevel() <= 21 ? null : ConscryptUtil.providerOrNull();
   }

   public static String toRsaSsaPkcs1Algo(RsaSsaPkcs1Parameters.HashType hashType) throws GeneralSecurityException {
      if (hashType == RsaSsaPkcs1Parameters.HashType.SHA256) {
         return "SHA256withRSA";
      } else if (hashType == RsaSsaPkcs1Parameters.HashType.SHA384) {
         return "SHA384withRSA";
      } else if (hashType == RsaSsaPkcs1Parameters.HashType.SHA512) {
         return "SHA512withRSA";
      } else {
         throw new GeneralSecurityException("unknown hash type");
      }
   }

   public static PublicKeyVerify create(RsaSsaPkcs1PublicKey key) throws GeneralSecurityException {
      Provider conscrypt = conscryptProviderOrNull();
      if (conscrypt == null) {
         throw new NoSuchProviderException("RSA-PKCS1.5 using Conscrypt is not supported.");
      } else {
         return createWithProvider(key, conscrypt);
      }
   }

   @AccessesPartialKey
   public static PublicKeyVerify createWithProvider(RsaSsaPkcs1PublicKey key, Provider conscrypt) throws GeneralSecurityException {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA", conscrypt);
      RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(new RSAPublicKeySpec(key.getModulus(), key.getParameters().getPublicExponent()));
      return new RsaSsaPkcs1VerifyConscrypt(
         publicKey,
         key.getParameters().getHashType(),
         key.getOutputPrefix().toByteArray(),
         key.getParameters().getVariant().equals(RsaSsaPkcs1Parameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY,
         conscrypt
      );
   }

   private RsaSsaPkcs1VerifyConscrypt(
      final RSAPublicKey pubKey, RsaSsaPkcs1Parameters.HashType hashType, byte[] outputPrefix, byte[] messageSuffix, Provider conscrypt
   ) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use RSA-PKCS1.5 in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         Validators.validateRsaModulusSize(pubKey.getModulus().bitLength());
         Validators.validateRsaPublicExponent(pubKey.getPublicExponent());
         this.publicKey = pubKey;
         this.signatureAlgorithm = toRsaSsaPkcs1Algo(hashType);
         this.outputPrefix = outputPrefix;
         this.messageSuffix = messageSuffix;
         this.conscrypt = conscrypt;
      }
   }

   @Override
   public void verify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
      if (!Util.isPrefix(this.outputPrefix, signature)) {
         throw new GeneralSecurityException("Invalid signature (output prefix mismatch)");
      } else {
         Signature verifier = Signature.getInstance(this.signatureAlgorithm, this.conscrypt);
         verifier.initVerify(this.publicKey);
         verifier.update(data);
         if (this.messageSuffix.length > 0) {
            verifier.update(this.messageSuffix);
         }

         boolean verified = false;

         try {
            byte[] signatureNoPrefix = Arrays.copyOfRange(signature, this.outputPrefix.length, signature.length);
            verified = verifier.verify(signatureNoPrefix);
         } catch (RuntimeException var6) {
            verified = false;
         }

         if (!verified) {
            throw new GeneralSecurityException("Invalid signature");
         }
      }
   }
}
