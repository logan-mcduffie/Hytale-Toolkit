package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.signature.MlDsaParameters;
import com.google.crypto.tink.signature.MlDsaPrivateKey;
import com.google.errorprone.annotations.Immutable;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;

@Immutable
public final class MlDsaSignConscrypt implements PublicKeySign {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private static final String TEST_WORKLOAD = "test workload";
   private final byte[] outputPrefix;
   private final PrivateKey privateKey;
   private final String algorithm;
   private final int signatureLength;
   private final Provider provider;

   private MlDsaSignConscrypt(byte[] outputPrefix, PrivateKey privateKey, String algorithm, int signatureLength, Provider provider) {
      this.outputPrefix = outputPrefix;
      this.privateKey = privateKey;
      this.algorithm = algorithm;
      this.signatureLength = signatureLength;
      this.provider = provider;
   }

   @AccessesPartialKey
   public static PublicKeySign createWithProvider(MlDsaPrivateKey mlDsaPrivateKey, Provider provider) throws GeneralSecurityException {
      if (provider == null) {
         throw new NullPointerException("provider must not be null");
      } else if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ML-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
      } else {
         MlDsaParameters.MlDsaInstance mlDsaInstance = mlDsaPrivateKey.getPublicKey().getParameters().getMlDsaInstance();
         if (mlDsaInstance != MlDsaParameters.MlDsaInstance.ML_DSA_65) {
            throw new GeneralSecurityException("Only ML-DSA-65 currently supported");
         } else {
            PrivateKey privateKey = KeyFactory.getInstance("ML-DSA-65", provider)
               .generatePrivate(new MlDsaVerifyConscrypt.RawKeySpec(mlDsaPrivateKey.getPrivateSeed().toByteArray(InsecureSecretKeyAccess.get())));
            byte[] testSignature = signInternal(
               "test workload".getBytes(StandardCharsets.UTF_8), mlDsaPrivateKey.getOutputPrefix().toByteArray(), privateKey, "ML-DSA-65", 3309, provider
            );
            MlDsaVerifyConscrypt verifier = (MlDsaVerifyConscrypt)MlDsaVerifyConscrypt.createWithProvider(mlDsaPrivateKey.getPublicKey(), provider);
            verifier.verify(testSignature, "test workload".getBytes(StandardCharsets.UTF_8));
            return new MlDsaSignConscrypt(mlDsaPrivateKey.getOutputPrefix().toByteArray(), privateKey, "ML-DSA-65", 3309, provider);
         }
      }
   }

   @AccessesPartialKey
   public static PublicKeySign create(MlDsaPrivateKey mlDsaPrivateKey) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ML-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
      } else {
         Provider provider = ConscryptUtil.providerOrNull();
         if (provider == null) {
            throw new GeneralSecurityException("Obtaining Conscrypt provider failed");
         } else {
            return createWithProvider(mlDsaPrivateKey, provider);
         }
      }
   }

   public static boolean isSupported() {
      return MlDsaVerifyConscrypt.isSupported();
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
