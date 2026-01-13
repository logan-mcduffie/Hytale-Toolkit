package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.internal.AesCtrHmacAeadProtoSerialization;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyKeyManagerImpl;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableKeyDerivationRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.subtle.EncryptThenAuthenticate;
import com.google.crypto.tink.util.SecretBytes;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class AesCtrHmacAeadKeyManager {
   private static final PrimitiveConstructor<AesCtrHmacAeadKey, Aead> AES_CTR_HMAC_AEAD_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      EncryptThenAuthenticate::create, AesCtrHmacAeadKey.class, Aead.class
   );
   private static final KeyManager<Aead> legacyKeyManager = LegacyKeyManagerImpl.create(
      getKeyType(), Aead.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.AesCtrHmacAeadKey.parser()
   );
   private static final MutableKeyDerivationRegistry.InsecureKeyCreator<AesCtrHmacAeadParameters> KEY_DERIVER = AesCtrHmacAeadKeyManager::createAesCtrHmacAeadKeyFromRandomness;
   private static final KeyCreator<AesCtrHmacAeadParameters> KEY_CREATOR = AesCtrHmacAeadKeyManager::createAesCtrHmacAeadKey;
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;

   private static void validate(AesCtrHmacAeadParameters parameters) throws GeneralSecurityException {
      if (parameters.getAesKeySizeBytes() != 16 && parameters.getAesKeySizeBytes() != 32) {
         throw new GeneralSecurityException("AES key size must be 16 or 32 bytes");
      }
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey";
   }

   @AccessesPartialKey
   static AesCtrHmacAeadKey createAesCtrHmacAeadKeyFromRandomness(
      AesCtrHmacAeadParameters parameters, InputStream stream, @Nullable Integer idRequirement, SecretKeyAccess access
   ) throws GeneralSecurityException {
      return AesCtrHmacAeadKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setAesKeyBytes(Util.readIntoSecretBytes(stream, parameters.getAesKeySizeBytes(), access))
         .setHmacKeyBytes(Util.readIntoSecretBytes(stream, parameters.getHmacKeySizeBytes(), access))
         .build();
   }

   @AccessesPartialKey
   static AesCtrHmacAeadKey createAesCtrHmacAeadKey(AesCtrHmacAeadParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      validate(parameters);
      return AesCtrHmacAeadKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setAesKeyBytes(SecretBytes.randomBytes(parameters.getAesKeySizeBytes()))
         .setHmacKeyBytes(SecretBytes.randomBytes(parameters.getHmacKeySizeBytes()))
         .build();
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("AES128_CTR_HMAC_SHA256", PredefinedAeadParameters.AES128_CTR_HMAC_SHA256);
      result.put(
         "AES128_CTR_HMAC_SHA256_RAW",
         AesCtrHmacAeadParameters.builder()
            .setAesKeySizeBytes(16)
            .setHmacKeySizeBytes(32)
            .setTagSizeBytes(16)
            .setIvSizeBytes(16)
            .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
            .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
            .build()
      );
      result.put("AES256_CTR_HMAC_SHA256", PredefinedAeadParameters.AES256_CTR_HMAC_SHA256);
      result.put(
         "AES256_CTR_HMAC_SHA256_RAW",
         AesCtrHmacAeadParameters.builder()
            .setAesKeySizeBytes(32)
            .setHmacKeySizeBytes(32)
            .setTagSizeBytes(32)
            .setIvSizeBytes(16)
            .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
            .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
            .build()
      );
      return Collections.unmodifiableMap(result);
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use AES-CTR-HMAC in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         AesCtrHmacAeadProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(AES_CTR_HMAC_AEAD_PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyDerivationRegistry.globalInstance().add(KEY_DERIVER, AesCtrHmacAeadParameters.class);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, AesCtrHmacAeadParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyKeyManager, FIPS, newKeyAllowed);
      }
   }

   public static final KeyTemplate aes128CtrHmacSha256Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesCtrHmacAeadParameters.builder()
               .setAesKeySizeBytes(16)
               .setHmacKeySizeBytes(32)
               .setIvSizeBytes(16)
               .setTagSizeBytes(16)
               .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
               .setVariant(AesCtrHmacAeadParameters.Variant.TINK)
               .build()
         )
      );
   }

   public static final KeyTemplate aes256CtrHmacSha256Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesCtrHmacAeadParameters.builder()
               .setAesKeySizeBytes(32)
               .setHmacKeySizeBytes(32)
               .setIvSizeBytes(16)
               .setTagSizeBytes(32)
               .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
               .setVariant(AesCtrHmacAeadParameters.Variant.TINK)
               .build()
         )
      );
   }

   private AesCtrHmacAeadKeyManager() {
   }
}
