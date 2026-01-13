package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.aead.internal.AesEaxProtoSerialization;
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
import com.google.crypto.tink.subtle.AesEaxJce;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class AesEaxKeyManager {
   private static final PrimitiveConstructor<AesEaxKey, Aead> AES_EAX_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      AesEaxJce::create, AesEaxKey.class, Aead.class
   );
   private static final KeyManager<Aead> legacyKeyManager = LegacyKeyManagerImpl.create(
      getKeyType(), Aead.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.AesEaxKey.parser()
   );
   private static final KeyCreator<AesEaxParameters> KEY_CREATOR = AesEaxKeyManager::createAesEaxKey;

   private static final void validate(AesEaxParameters parameters) throws GeneralSecurityException {
      if (parameters.getKeySizeBytes() == 24) {
         throw new GeneralSecurityException("192 bit AES GCM Parameters are not valid");
      }
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.AesEaxKey";
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("AES128_EAX", PredefinedAeadParameters.AES128_EAX);
      result.put(
         "AES128_EAX_RAW",
         AesEaxParameters.builder().setIvSizeBytes(16).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesEaxParameters.Variant.NO_PREFIX).build()
      );
      result.put("AES256_EAX", PredefinedAeadParameters.AES256_EAX);
      result.put(
         "AES256_EAX_RAW",
         AesEaxParameters.builder().setIvSizeBytes(16).setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesEaxParameters.Variant.NO_PREFIX).build()
      );
      return Collections.unmodifiableMap(result);
   }

   @AccessesPartialKey
   private static AesEaxKey createAesEaxKey(AesEaxParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      validate(parameters);
      return AesEaxKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setKeyBytes(SecretBytes.randomBytes(parameters.getKeySizeBytes()))
         .build();
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering AES EAX is not supported in FIPS mode");
      } else {
         AesEaxProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(AES_EAX_PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, AesEaxParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyKeyManager, newKeyAllowed);
      }
   }

   public static final KeyTemplate aes128EaxTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesEaxParameters.builder().setIvSizeBytes(16).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesEaxParameters.Variant.TINK).build()
         )
      );
   }

   public static final KeyTemplate rawAes128EaxTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesEaxParameters.builder().setIvSizeBytes(16).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesEaxParameters.Variant.NO_PREFIX).build()
         )
      );
   }

   public static final KeyTemplate aes256EaxTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesEaxParameters.builder().setIvSizeBytes(16).setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesEaxParameters.Variant.TINK).build()
         )
      );
   }

   public static final KeyTemplate rawAes256EaxTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesEaxParameters.builder().setIvSizeBytes(16).setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesEaxParameters.Variant.NO_PREFIX).build()
         )
      );
   }

   private AesEaxKeyManager() {
   }
}
