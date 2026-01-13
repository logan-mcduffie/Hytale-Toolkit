package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyKeyManagerImpl;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.streamingaead.internal.AesCtrHmacStreamingProtoSerialization;
import com.google.crypto.tink.subtle.AesCtrHmacStreaming;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class AesCtrHmacStreamingKeyManager {
   private static final PrimitiveConstructor<AesCtrHmacStreamingKey, StreamingAead> AES_CTR_HMAC_STREAMING_AEAD_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      AesCtrHmacStreaming::create, AesCtrHmacStreamingKey.class, StreamingAead.class
   );
   private static final KeyCreator<AesCtrHmacStreamingParameters> KEY_CREATOR = AesCtrHmacStreamingKeyManager::createAesCtrHmacStreamingKey;
   private static final KeyManager<StreamingAead> legacyKeyManager = LegacyKeyManagerImpl.create(
      getKeyType(), StreamingAead.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.AesCtrHmacStreamingKey.parser()
   );

   @AccessesPartialKey
   private static AesCtrHmacStreamingKey createAesCtrHmacStreamingKey(AesCtrHmacStreamingParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      return AesCtrHmacStreamingKey.create(parameters, SecretBytes.randomBytes(parameters.getKeySizeBytes()));
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey";
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("AES128_CTR_HMAC_SHA256_4KB", PredefinedStreamingAeadParameters.AES128_CTR_HMAC_SHA256_4KB);
      result.put("AES128_CTR_HMAC_SHA256_1MB", PredefinedStreamingAeadParameters.AES128_CTR_HMAC_SHA256_1MB);
      result.put("AES256_CTR_HMAC_SHA256_4KB", PredefinedStreamingAeadParameters.AES256_CTR_HMAC_SHA256_4KB);
      result.put("AES256_CTR_HMAC_SHA256_1MB", PredefinedStreamingAeadParameters.AES256_CTR_HMAC_SHA256_1MB);
      return Collections.unmodifiableMap(result);
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering AES CTR HMAC Streaming AEAD is not supported in FIPS mode");
      } else {
         AesCtrHmacStreamingProtoSerialization.register();
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, AesCtrHmacStreamingParameters.class);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(AES_CTR_HMAC_STREAMING_AEAD_PRIMITIVE_CONSTRUCTOR);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyKeyManager, newKeyAllowed);
      }
   }

   public static final KeyTemplate aes128CtrHmacSha2564KBTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesCtrHmacStreamingParameters.builder()
               .setKeySizeBytes(16)
               .setDerivedKeySizeBytes(16)
               .setHkdfHashType(AesCtrHmacStreamingParameters.HashType.SHA256)
               .setHmacHashType(AesCtrHmacStreamingParameters.HashType.SHA256)
               .setHmacTagSizeBytes(32)
               .setCiphertextSegmentSizeBytes(4096)
               .build()
         )
      );
   }

   public static final KeyTemplate aes128CtrHmacSha2561MBTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesCtrHmacStreamingParameters.builder()
               .setKeySizeBytes(16)
               .setDerivedKeySizeBytes(16)
               .setHkdfHashType(AesCtrHmacStreamingParameters.HashType.SHA256)
               .setHmacHashType(AesCtrHmacStreamingParameters.HashType.SHA256)
               .setHmacTagSizeBytes(32)
               .setCiphertextSegmentSizeBytes(1048576)
               .build()
         )
      );
   }

   public static final KeyTemplate aes256CtrHmacSha2564KBTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesCtrHmacStreamingParameters.builder()
               .setKeySizeBytes(32)
               .setDerivedKeySizeBytes(32)
               .setHkdfHashType(AesCtrHmacStreamingParameters.HashType.SHA256)
               .setHmacHashType(AesCtrHmacStreamingParameters.HashType.SHA256)
               .setHmacTagSizeBytes(32)
               .setCiphertextSegmentSizeBytes(4096)
               .build()
         )
      );
   }

   public static final KeyTemplate aes256CtrHmacSha2561MBTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesCtrHmacStreamingParameters.builder()
               .setKeySizeBytes(32)
               .setDerivedKeySizeBytes(32)
               .setHkdfHashType(AesCtrHmacStreamingParameters.HashType.SHA256)
               .setHmacHashType(AesCtrHmacStreamingParameters.HashType.SHA256)
               .setHmacTagSizeBytes(32)
               .setCiphertextSegmentSizeBytes(1048576)
               .build()
         )
      );
   }

   private AesCtrHmacStreamingKeyManager() {
   }
}
