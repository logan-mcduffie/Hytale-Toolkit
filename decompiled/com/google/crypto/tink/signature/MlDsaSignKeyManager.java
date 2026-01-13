package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.signature.internal.MlDsaProtoSerialization;
import com.google.crypto.tink.signature.internal.MlDsaVerifyConscrypt;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.util.Map;
import javax.annotation.Nullable;

final class MlDsaSignKeyManager {
   static final String ML_DSA_65_ALGORITHM = "ML-DSA-65";
   private static final KeyCreator<MlDsaParameters> KEY_CREATOR = MlDsaSignKeyManager::createKey;
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;

   static String getPublicKeyType() {
      return "type.googleapis.com/google.crypto.tink.MlDsaPublicKey";
   }

   static String getPrivateKeyType() {
      return "type.googleapis.com/google.crypto.tink.MlDsaPrivateKey";
   }

   @AccessesPartialKey
   private static MlDsaPrivateKey createKey(MlDsaParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      Provider provider = ConscryptUtil.providerOrNull();
      if (provider == null) {
         throw new GeneralSecurityException("Obtaining Conscrypt provider failed");
      } else {
         KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ML-DSA-65", provider);
         KeyPair keyPair = keyPairGenerator.generateKeyPair();
         KeyFactory keyFactory = KeyFactory.getInstance("ML-DSA-65", provider);
         MlDsaPublicKey publicKey = MlDsaPublicKey.builder()
            .setSerializedPublicKey(Bytes.copyFrom(keyFactory.getKeySpec(keyPair.getPublic(), MlDsaVerifyConscrypt.RawKeySpec.class).getEncoded()))
            .setParameters(parameters)
            .setIdRequirement(idRequirement)
            .build();
         SecretBytes privateSeed = SecretBytes.copyFrom(
            keyFactory.getKeySpec(keyPair.getPrivate(), MlDsaVerifyConscrypt.RawKeySpec.class).getEncoded(), InsecureSecretKeyAccess.get()
         );
         return MlDsaPrivateKey.createWithoutVerification(publicKey, privateSeed);
      }
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      return Map.of(
         "ML_DSA_65",
         MlDsaParameters.create(MlDsaParameters.MlDsaInstance.ML_DSA_65, MlDsaParameters.Variant.TINK),
         "ML_DSA_65_RAW",
         MlDsaParameters.create(MlDsaParameters.MlDsaInstance.ML_DSA_65, MlDsaParameters.Variant.NO_PREFIX)
      );
   }

   public static void registerPair() throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Cannot use ML-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
      } else if (ConscryptUtil.providerOrNull() == null) {
         throw new GeneralSecurityException("Cannot use ML-DSA without Conscrypt provider");
      } else {
         MlDsaProtoSerialization.register();
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, MlDsaParameters.class);
      }
   }

   private MlDsaSignKeyManager() {
   }
}
