package com.google.crypto.tink.daead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.daead.internal.AesSivProtoSerialization;
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
import com.google.crypto.tink.subtle.AesSiv;
import com.google.crypto.tink.util.SecretBytes;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class AesSivKeyManager {
   private static final PrimitiveConstructor<AesSivKey, DeterministicAead> AES_SIV_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      AesSivKeyManager::createDeterministicAead, AesSivKey.class, DeterministicAead.class
   );
   private static final int KEY_SIZE_IN_BYTES = 64;
   private static final KeyManager<DeterministicAead> legacyKeyManager = LegacyKeyManagerImpl.create(
      getKeyType(), DeterministicAead.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.AesSivKey.parser()
   );
   private static final MutableKeyDerivationRegistry.InsecureKeyCreator<AesSivParameters> KEY_DERIVER = AesSivKeyManager::createAesSivKeyFromRandomness;
   private static final KeyCreator<AesSivParameters> KEY_CREATOR = AesSivKeyManager::newKey;

   private static DeterministicAead createDeterministicAead(AesSivKey key) throws GeneralSecurityException {
      validateParameters(key.getParameters());
      return AesSiv.create(key);
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.AesSivKey";
   }

   private static void validateParameters(AesSivParameters parameters) throws GeneralSecurityException {
      if (parameters.getKeySizeBytes() != 64) {
         throw new InvalidAlgorithmParameterException("invalid key size: " + parameters.getKeySizeBytes() + ". Valid keys must have " + 64 + " bytes.");
      }
   }

   @AccessesPartialKey
   static AesSivKey createAesSivKeyFromRandomness(AesSivParameters parameters, InputStream stream, @Nullable Integer idRequirement, SecretKeyAccess access) throws GeneralSecurityException {
      validateParameters(parameters);
      return AesSivKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setKeyBytes(Util.readIntoSecretBytes(stream, parameters.getKeySizeBytes(), access))
         .build();
   }

   @AccessesPartialKey
   static AesSivKey newKey(AesSivParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      validateParameters(parameters);
      return AesSivKey.builder()
         .setParameters(parameters)
         .setIdRequirement(idRequirement)
         .setKeyBytes(SecretBytes.randomBytes(parameters.getKeySizeBytes()))
         .build();
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("AES256_SIV", PredefinedDeterministicAeadParameters.AES256_SIV);
      result.put("AES256_SIV_RAW", AesSivParameters.builder().setKeySizeBytes(64).setVariant(AesSivParameters.Variant.NO_PREFIX).build());
      return Collections.unmodifiableMap(result);
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering AES SIV is not supported in FIPS mode");
      } else {
         AesSivProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(AES_SIV_PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyDerivationRegistry.globalInstance().add(KEY_DERIVER, AesSivParameters.class);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, AesSivParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyKeyManager, newKeyAllowed);
      }
   }

   public static final KeyTemplate aes256SivTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(AesSivParameters.builder().setKeySizeBytes(64).setVariant(AesSivParameters.Variant.TINK).build())
      );
   }

   public static final KeyTemplate rawAes256SivTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(AesSivParameters.builder().setKeySizeBytes(64).setVariant(AesSivParameters.Variant.NO_PREFIX).build())
      );
   }

   private AesSivKeyManager() {
   }
}
