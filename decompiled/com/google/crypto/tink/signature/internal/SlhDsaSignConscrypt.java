package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.signature.SlhDsaParameters;
import com.google.crypto.tink.signature.SlhDsaPrivateKey;
import com.google.errorprone.annotations.Immutable;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;

@Immutable
public class SlhDsaSignConscrypt implements PublicKeySign {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private static final String TEST_WORKLOAD = "test workload";
   private final byte[] outputPrefix;
   private final PrivateKey privateKey;
   private final String algorithm;
   private final int signatureLength;
   private final Provider provider;

   public SlhDsaSignConscrypt(byte[] outputPrefix, PrivateKey privateKey, String algorithm, int signatureLength, Provider provider) {
      this.outputPrefix = outputPrefix;
      this.privateKey = privateKey;
      this.algorithm = algorithm;
      this.signatureLength = signatureLength;
      this.provider = provider;
   }

   @AccessesPartialKey
   public static PublicKeySign createWithProvider(SlhDsaPrivateKey slhDsaPrivateKey, Provider provider) throws GeneralSecurityException {
      if (provider == null) {
         throw new NullPointerException("provider must not be null");
      } else if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use SLH-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
      } else {
         SlhDsaParameters parameters = slhDsaPrivateKey.getParameters();
         if (parameters.getPrivateKeySize() == 64
            && parameters.getHashType() == SlhDsaParameters.HashType.SHA2
            && parameters.getSignatureType() == SlhDsaParameters.SignatureType.SMALL_SIGNATURE) {
            PrivateKey privateKey = KeyFactory.getInstance("SLH-DSA-SHA2-128S", provider)
               .generatePrivate(new SlhDsaVerifyConscrypt.RawKeySpec(slhDsaPrivateKey.getPrivateKeyBytes().toByteArray(InsecureSecretKeyAccess.get())));
            byte[] testSignature = signInternal(
               "test workload".getBytes(StandardCharsets.UTF_8),
               slhDsaPrivateKey.getOutputPrefix().toByteArray(),
               privateKey,
               "SLH-DSA-SHA2-128S",
               7856,
               provider
            );
            SlhDsaVerifyConscrypt verifier = (SlhDsaVerifyConscrypt)SlhDsaVerifyConscrypt.createWithProvider(slhDsaPrivateKey.getPublicKey(), provider);
            verifier.verify(testSignature, "test workload".getBytes(StandardCharsets.UTF_8));
            return new SlhDsaSignConscrypt(slhDsaPrivateKey.getOutputPrefix().toByteArray(), privateKey, "SLH-DSA-SHA2-128S", 7856, provider);
         } else {
            throw new GeneralSecurityException("Unsupported SLH-DSA parameters");
         }
      }
   }

   @AccessesPartialKey
   public static PublicKeySign create(SlhDsaPrivateKey slhDsaPrivateKey) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use SLH-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
      } else {
         Provider provider = ConscryptUtil.providerOrNull();
         if (provider == null) {
            throw new GeneralSecurityException("Obtaining Conscrypt provider failed");
         } else {
            return createWithProvider(slhDsaPrivateKey, provider);
         }
      }
   }

   public static boolean isSupported() {
      return SlhDsaVerifyConscrypt.isSupported();
   }

   @Override
   public byte[] sign(final byte[] data) throws GeneralSecurityException {
      return signInternal(data, this.outputPrefix, this.privateKey, this.algorithm, this.signatureLength, this.provider);
   }

   private static byte[] signInternal(byte[] data, byte[] outputPrefix, PrivateKey privateKey, String algorithm, int signatureLength, Provider provider) throws GeneralSecurityException {
      Signature signer = Signature.getInstance(algorithm, provider);
      signer.initSign(privateKey);
      signer.update(data);
      byte[] signature = new byte[outputPrefix.length + signatureLength];
      if (outputPrefix.length > 0) {
         System.arraycopy(outputPrefix, 0, signature, 0, outputPrefix.length);
      }

      signer.sign(signature, outputPrefix.length, signatureLength);
      return signature;
   }
}
