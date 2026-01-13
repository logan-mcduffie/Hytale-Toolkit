package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.internal.AesGcmSivProtoSerialization;
import com.google.crypto.tink.aead.subtle.AesGcmSiv;
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
import com.google.crypto.tink.util.SecretBytes;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class AesGcmSivKeyManager {
   private static final PrimitiveConstructor<AesGcmSivKey, Aead> AES_GCM_SIV_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      AesGcmSiv::create, AesGcmSivKey.class, Aead.class
   );
   private static final KeyCreator<AesGcmSivParameters> KEY_CREATOR = AesGcmSivKeyManager::createAesGcmSivKey;
   private static final MutableKeyDerivationRegistry.InsecureKeyCreator<AesGcmSivParameters> KEY_DERIVER = AesGcmSivKeyManager::createAesGcmSivKeyFromRandomness;
   private static final KeyManager<Aead> legacyKeyManager = LegacyKeyManagerImpl.create(
      "type.googleapis.com/google.crypto.tink.AesGcmSivKey", Aead.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.AesGcmSivKey.parser()
   );

   @AccessesPartialKey
   static AesGcmSivKey createAesGcmSivKeyFromRandomness(
      AesGcmSivParameters parameters, InputStream stream, @Nullable Integer idRequirement, SecretKeyAccess access
   ) throws GeneralSecurityException {
      return AesGcmSivKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setKeyBytes(Util.readIntoSecretBytes(stream, parameters.getKeySizeBytes(), access))
         .build();
   }

   @AccessesPartialKey
   private static AesGcmSivKey createAesGcmSivKey(AesGcmSivParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      return AesGcmSivKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setKeyBytes(SecretBytes.randomBytes(parameters.getKeySizeBytes()))
         .build();
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("AES128_GCM_SIV", AesGcmSivParameters.builder().setKeySizeBytes(16).setVariant(AesGcmSivParameters.Variant.TINK).build());
      result.put("AES128_GCM_SIV_RAW", AesGcmSivParameters.builder().setKeySizeBytes(16).setVariant(AesGcmSivParameters.Variant.NO_PREFIX).build());
      result.put("AES256_GCM_SIV", AesGcmSivParameters.builder().setKeySizeBytes(32).setVariant(AesGcmSivParameters.Variant.TINK).build());
      result.put("AES256_GCM_SIV_RAW", AesGcmSivParameters.builder().setKeySizeBytes(32).setVariant(AesGcmSivParameters.Variant.NO_PREFIX).build());
      return Collections.unmodifiableMap(result);
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering AES GCM SIV is not supported in FIPS mode");
      } else {
         AesGcmSivProtoSerialization.register();
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyDerivationRegistry.globalInstance().add(KEY_DERIVER, AesGcmSivParameters.class);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, AesGcmSivParameters.class);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(AES_GCM_SIV_PRIMITIVE_CONSTRUCTOR);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyKeyManager, newKeyAllowed);
      }
   }

   public static final KeyTemplate aes128GcmSivTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(AesGcmSivParameters.builder().setKeySizeBytes(16).setVariant(AesGcmSivParameters.Variant.TINK).build())
      );
   }

   public static final KeyTemplate rawAes128GcmSivTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(AesGcmSivParameters.builder().setKeySizeBytes(16).setVariant(AesGcmSivParameters.Variant.NO_PREFIX).build())
      );
   }

   public static final KeyTemplate aes256GcmSivTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(AesGcmSivParameters.builder().setKeySizeBytes(32).setVariant(AesGcmSivParameters.Variant.TINK).build())
      );
   }

   public static final KeyTemplate rawAes256GcmSivTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(AesGcmSivParameters.builder().setKeySizeBytes(32).setVariant(AesGcmSivParameters.Variant.NO_PREFIX).build())
      );
   }

   private AesGcmSivKeyManager() {
   }
}
