package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.signature.RsaSsaPssParameters;
import com.google.crypto.tink.signature.RsaSsaPssPrivateKey;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.Validators;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PSSParameterSpec;
import java.security.spec.RSAPrivateCrtKeySpec;

@Immutable
public final class RsaSsaPssSignConscrypt implements PublicKeySign {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private static final byte[] EMPTY = new byte[0];
   private static final byte[] legacyMessageSuffix = new byte[]{0};
   private final RSAPrivateCrtKey privateKey;
   private final String signatureAlgorithm;
   private final PSSParameterSpec parameterSpec;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;
   private final Provider conscrypt;

   public static PublicKeySign create(RsaSsaPssPrivateKey key) throws GeneralSecurityException {
      Provider conscrypt = RsaSsaPssVerifyConscrypt.conscryptProviderOrNull();
      return createWithProvider(key, conscrypt);
   }

   @AccessesPartialKey
   public static PublicKeySign createWithProvider(RsaSsaPssPrivateKey key, Provider conscrypt) throws GeneralSecurityException {
      if (conscrypt == null) {
         throw new NoSuchProviderException("RSA SSA PSS using Conscrypt is not supported.");
      } else {
         KeyFactory keyFactory = KeyFactory.getInstance("RSA", conscrypt);
         RsaSsaPssParameters params = key.getParameters();
         RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey)keyFactory.generatePrivate(
            new RSAPrivateCrtKeySpec(
               key.getPublicKey().getModulus(),
               params.getPublicExponent(),
               key.getPrivateExponent().getBigInteger(InsecureSecretKeyAccess.get()),
               key.getPrimeP().getBigInteger(InsecureSecretKeyAccess.get()),
               key.getPrimeQ().getBigInteger(InsecureSecretKeyAccess.get()),
               key.getPrimeExponentP().getBigInteger(InsecureSecretKeyAccess.get()),
               key.getPrimeExponentQ().getBigInteger(InsecureSecretKeyAccess.get()),
               key.getCrtCoefficient().getBigInteger(InsecureSecretKeyAccess.get())
            )
         );
         return new RsaSsaPssSignConscrypt(
            privateKey,
            params.getSigHashType(),
            params.getMgf1HashType(),
            params.getSaltLengthBytes(),
            key.getOutputPrefix().toByteArray(),
            params.getVariant().equals(RsaSsaPssParameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY,
            conscrypt
         );
      }
   }

   private RsaSsaPssSignConscrypt(
      final RSAPrivateCrtKey privateKey,
      RsaSsaPssParameters.HashType sigHash,
      RsaSsaPssParameters.HashType mgf1Hash,
      int saltLength,
      byte[] outputPrefix,
      byte[] messageSuffix,
      Provider conscrypt
   ) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Cannot use RSA PSS in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         Validators.validateRsaModulusSize(privateKey.getModulus().bitLength());
         Validators.validateRsaPublicExponent(privateKey.getPublicExponent());
         this.privateKey = privateKey;
         this.signatureAlgorithm = RsaSsaPssVerifyConscrypt.getConscryptRsaSsaPssAlgo(sigHash);
         this.parameterSpec = RsaSsaPssVerifyConscrypt.getPssParameterSpec(sigHash, mgf1Hash, saltLength);
         this.outputPrefix = outputPrefix;
         this.messageSuffix = messageSuffix;
         this.conscrypt = conscrypt;
      }
   }

   @Override
   public byte[] sign(final byte[] data) throws GeneralSecurityException {
      Signature signer = Signature.getInstance(this.signatureAlgorithm, this.conscrypt);
      signer.initSign(this.privateKey);
      signer.setParameter(this.parameterSpec);
      signer.update(data);
      if (this.messageSuffix.length > 0) {
         signer.update(this.messageSuffix);
      }

      byte[] signature = signer.sign();
      return this.outputPrefix.length == 0 ? signature : Bytes.concat(this.outputPrefix, signature);
   }
}
