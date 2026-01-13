package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.internal.AesGcmProtoSerialization;
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
import com.google.crypto.tink.subtle.AesGcmJce;
import com.google.crypto.tink.util.SecretBytes;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class AesGcmKeyManager {
   private static final PrimitiveConstructor<AesGcmKey, Aead> AES_GCM_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      AesGcmJce::create, AesGcmKey.class, Aead.class
   );
   private static final KeyManager<Aead> legacyKeyManager = LegacyKeyManagerImpl.create(
      getKeyType(), Aead.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.AesGcmKey.parser()
   );
   private static final MutableKeyDerivationRegistry.InsecureKeyCreator<AesGcmParameters> KEY_DERIVER = AesGcmKeyManager::createAesGcmKeyFromRandomness;
   private static final KeyCreator<AesGcmParameters> KEY_CREATOR = AesGcmKeyManager::createAesGcmKey;
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;

   private static final void validate(AesGcmParameters parameters) throws GeneralSecurityException {
      if (parameters.getKeySizeBytes() == 24) {
         throw new GeneralSecurityException("192 bit AES GCM Parameters are not valid");
      }
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.AesGcmKey";
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("AES128_GCM", PredefinedAeadParameters.AES128_GCM);
      result.put(
         "AES128_GCM_RAW",
         AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
      );
      result.put("AES256_GCM", PredefinedAeadParameters.AES256_GCM);
      result.put(
         "AES256_GCM_RAW",
         AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
      );
      return Collections.unmodifiableMap(result);
   }

   @AccessesPartialKey
   static AesGcmKey createAesGcmKeyFromRandomness(AesGcmParameters parameters, InputStream stream, @Nullable Integer idRequirement, SecretKeyAccess access) throws GeneralSecurityException {
      validate(parameters);
      return AesGcmKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setKeyBytes(Util.readIntoSecretBytes(stream, parameters.getKeySizeBytes(), access))
         .build();
   }

   @AccessesPartialKey
   private static AesGcmKey createAesGcmKey(AesGcmParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      validate(parameters);
      return AesGcmKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setKeyBytes(SecretBytes.randomBytes(parameters.getKeySizeBytes()))
         .build();
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use AES-GCM in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         AesGcmProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(AES_GCM_PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyDerivationRegistry.globalInstance().add(KEY_DERIVER, AesGcmParameters.class);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, AesGcmParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyKeyManager, FIPS, newKeyAllowed);
      }
   }

   public static final KeyTemplate aes128GcmTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.TINK).build()
         )
      );
   }

   public static final KeyTemplate rawAes128GcmTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
         )
      );
   }

   public static final KeyTemplate aes256GcmTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.TINK).build()
         )
      );
   }

   public static final KeyTemplate rawAes256GcmTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
         )
      );
   }

   private AesGcmKeyManager() {
   }
}
