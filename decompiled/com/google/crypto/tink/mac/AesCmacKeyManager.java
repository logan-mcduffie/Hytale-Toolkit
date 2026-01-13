package com.google.crypto.tink.mac;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyKeyManagerImpl;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.mac.internal.AesCmacProtoSerialization;
import com.google.crypto.tink.mac.internal.ChunkedAesCmacImpl;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.subtle.PrfMac;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class AesCmacKeyManager {
   private static final int KEY_SIZE_IN_BYTES = 32;
   private static final KeyCreator<AesCmacParameters> KEY_CREATOR = AesCmacKeyManager::createAesCmacKey;
   private static final PrimitiveConstructor<AesCmacKey, ChunkedMac> CHUNKED_MAC_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      AesCmacKeyManager::createChunkedMac, AesCmacKey.class, ChunkedMac.class
   );
   private static final PrimitiveConstructor<AesCmacKey, Mac> MAC_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      AesCmacKeyManager::createMac, AesCmacKey.class, Mac.class
   );
   private static final KeyManager<Mac> legacyKeyManager = LegacyKeyManagerImpl.create(
      "type.googleapis.com/google.crypto.tink.AesCmacKey", Mac.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.AesCmacKey.parser()
   );

   private static void validateParameters(AesCmacParameters parameters) throws GeneralSecurityException {
      if (parameters.getKeySizeBytes() != 32) {
         throw new GeneralSecurityException("AesCmacKey size wrong, must be 32 bytes");
      }
   }

   @AccessesPartialKey
   private static AesCmacKey createAesCmacKey(AesCmacParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      validateParameters(parameters);
      return AesCmacKey.builder()
         .setParameters(parameters)
         .setAesKeyBytes(SecretBytes.randomBytes(parameters.getKeySizeBytes()))
         .setIdRequirement(idRequirement)
         .build();
   }

   private static ChunkedMac createChunkedMac(AesCmacKey key) throws GeneralSecurityException {
      validateParameters(key.getParameters());
      return ChunkedAesCmacImpl.create(key);
   }

   private static Mac createMac(AesCmacKey key) throws GeneralSecurityException {
      validateParameters(key.getParameters());
      return PrfMac.create(key);
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering AES CMAC is not supported in FIPS mode");
      } else {
         AesCmacProtoSerialization.register();
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, AesCmacParameters.class);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(CHUNKED_MAC_PRIMITIVE_CONSTRUCTOR);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(MAC_PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyKeyManager, newKeyAllowed);
      }
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("AES_CMAC", PredefinedMacParameters.AES_CMAC);
      result.put("AES256_CMAC", PredefinedMacParameters.AES_CMAC);
      result.put("AES256_CMAC_RAW", AesCmacParameters.builder().setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesCmacParameters.Variant.NO_PREFIX).build());
      return Collections.unmodifiableMap(result);
   }

   public static final KeyTemplate aes256CmacTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(AesCmacParameters.builder().setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesCmacParameters.Variant.TINK).build())
      );
   }

   public static final KeyTemplate rawAes256CmacTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesCmacParameters.builder().setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesCmacParameters.Variant.NO_PREFIX).build()
         )
      );
   }

   private AesCmacKeyManager() {
   }
}
