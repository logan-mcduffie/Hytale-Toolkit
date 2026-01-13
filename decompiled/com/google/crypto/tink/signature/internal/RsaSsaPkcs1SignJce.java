package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.signature.RsaSsaPkcs1Parameters;
import com.google.crypto.tink.signature.RsaSsaPkcs1PrivateKey;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.RsaSsaPkcs1VerifyJce;
import com.google.crypto.tink.subtle.Validators;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import javax.annotation.Nullable;

@Immutable
public final class RsaSsaPkcs1SignJce implements PublicKeySign {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private static final byte[] EMPTY = new byte[0];
   private static final byte[] legacyMessageSuffix = new byte[]{0};
   private static final byte[] testData = new byte[]{1, 2, 3};
   private final RSAPrivateCrtKey privateKey;
   private final String signatureAlgorithm;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;
   private final PublicKeyVerify verifier;
   @Nullable
   Provider conscryptOrNull;

   private static void validateHash(RsaSsaPkcs1Parameters.HashType hash) throws GeneralSecurityException {
      if (hash != RsaSsaPkcs1Parameters.HashType.SHA256 && hash != RsaSsaPkcs1Parameters.HashType.SHA384 && hash != RsaSsaPkcs1Parameters.HashType.SHA512) {
         throw new GeneralSecurityException("Unsupported hash: " + hash);
      }
   }

   private RsaSsaPkcs1SignJce(
      final RSAPrivateCrtKey privateKey,
      RsaSsaPkcs1Parameters.HashType hash,
      byte[] outputPrefix,
      byte[] messageSuffix,
      PublicKeyVerify verifier,
      @Nullable Provider conscryptOrNull
   ) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use RSA PKCS1.5 in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         validateHash(hash);
         Validators.validateRsaModulusSize(privateKey.getModulus().bitLength());
         Validators.validateRsaPublicExponent(privateKey.getPublicExponent());
         this.privateKey = privateKey;
         this.signatureAlgorithm = RsaSsaPkcs1VerifyConscrypt.toRsaSsaPkcs1Algo(hash);
         this.outputPrefix = outputPrefix;
         this.messageSuffix = messageSuffix;
         this.verifier = verifier;
         this.conscryptOrNull = conscryptOrNull;
      }
   }

   public static PublicKeySign create(RsaSsaPkcs1PrivateKey key) throws GeneralSecurityException {
      Provider conscryptOrNull = RsaSsaPkcs1VerifyConscrypt.conscryptProviderOrNull();
      return createWithProviderOrNull(key, conscryptOrNull);
   }

   public static PublicKeySign createWithProvider(RsaSsaPkcs1PrivateKey key, Provider provider) throws GeneralSecurityException {
      if (provider == null) {
         throw new NullPointerException("provider must not be null");
      } else {
         return createWithProviderOrNull(key, provider);
      }
   }

   @AccessesPartialKey
   private static PublicKeySign createWithProviderOrNull(RsaSsaPkcs1PrivateKey key, @Nullable Provider providerOrNull) throws GeneralSecurityException {
      KeyFactory keyFactory;
      if (providerOrNull != null) {
         keyFactory = KeyFactory.getInstance("RSA", providerOrNull);
      } else {
         keyFactory = EngineFactory.KEY_FACTORY.getInstance("RSA");
      }

      RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey)keyFactory.generatePrivate(
         new RSAPrivateCrtKeySpec(
            key.getPublicKey().getModulus(),
            key.getParameters().getPublicExponent(),
            key.getPrivateExponent().getBigInteger(InsecureSecretKeyAccess.get()),
            key.getPrimeP().getBigInteger(InsecureSecretKeyAccess.get()),
            key.getPrimeQ().getBigInteger(InsecureSecretKeyAccess.get()),
            key.getPrimeExponentP().getBigInteger(InsecureSecretKeyAccess.get()),
            key.getPrimeExponentQ().getBigInteger(InsecureSecretKeyAccess.get()),
            key.getCrtCoefficient().getBigInteger(InsecureSecretKeyAccess.get())
         )
      );
      PublicKeyVerify verifier;
      if (providerOrNull != null) {
         verifier = RsaSsaPkcs1VerifyConscrypt.createWithProvider(key.getPublicKey(), providerOrNull);
      } else {
         verifier = RsaSsaPkcs1VerifyJce.create(key.getPublicKey());
      }

      PublicKeySign signer = new RsaSsaPkcs1SignJce(
         privateKey,
         key.getParameters().getHashType(),
         key.getOutputPrefix().toByteArray(),
         key.getParameters().getVariant().equals(RsaSsaPkcs1Parameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY,
         verifier,
         providerOrNull
      );
      byte[] unused = signer.sign(testData);
      return signer;
   }

   private Signature getSignature() throws GeneralSecurityException {
      return this.conscryptOrNull != null
         ? Signature.getInstance(this.signatureAlgorithm, this.conscryptOrNull)
         : EngineFactory.SIGNATURE.getInstance(this.signatureAlgorithm);
   }

   @Override
   public byte[] sign(final byte[] data) throws GeneralSecurityException {
      Signature signer = this.getSignature();
      signer.initSign(this.privateKey);
      signer.update(data);
      if (this.messageSuffix.length > 0) {
         signer.update(this.messageSuffix);
      }

      byte[] signature = signer.sign();
      if (this.outputPrefix.length > 0) {
         signature = Bytes.concat(this.outputPrefix, signature);
      }

      try {
         this.verifier.verify(signature, data);
         return signature;
      } catch (GeneralSecurityException var5) {
         throw new IllegalStateException("RSA signature computation error", var5);
      }
   }
}
