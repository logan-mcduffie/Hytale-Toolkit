package com.google.crypto.tink.mac;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.SecretKeyAccess;
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
import com.google.crypto.tink.mac.internal.ChunkedHmacImpl;
import com.google.crypto.tink.mac.internal.HmacProtoSerialization;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.subtle.PrfMac;
import com.google.crypto.tink.util.SecretBytes;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class HmacKeyManager {
   private static final PrimitiveConstructor<HmacKey, ChunkedMac> CHUNKED_MAC_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      ChunkedHmacImpl::new, HmacKey.class, ChunkedMac.class
   );
   private static final PrimitiveConstructor<HmacKey, Mac> MAC_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(PrfMac::create, HmacKey.class, Mac.class);
   private static final KeyManager<Mac> legacyKeyManager = LegacyKeyManagerImpl.create(
      "type.googleapis.com/google.crypto.tink.HmacKey", Mac.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.HmacKey.parser()
   );
   private static final MutableKeyDerivationRegistry.InsecureKeyCreator<HmacParameters> KEY_DERIVER = HmacKeyManager::createHmacKeyFromRandomness;
   private static final KeyCreator<HmacParameters> KEY_CREATOR = HmacKeyManager::createNewHmacKey;
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.HmacKey";
   }

   @AccessesPartialKey
   static HmacKey createHmacKeyFromRandomness(HmacParameters parameters, InputStream stream, @Nullable Integer idRequirement, SecretKeyAccess access) throws GeneralSecurityException {
      return HmacKey.builder()
         .setParameters(parameters)
         .setKeyBytes(Util.readIntoSecretBytes(stream, parameters.getKeySizeBytes(), access))
         .setIdRequirement(idRequirement)
         .build();
   }

   @AccessesPartialKey
   static HmacKey createNewHmacKey(HmacParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      return HmacKey.builder()
         .setParameters(parameters)
         .setKeyBytes(SecretBytes.randomBytes(parameters.getKeySizeBytes()))
         .setIdRequirement(idRequirement)
         .build();
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("HMAC_SHA256_128BITTAG", PredefinedMacParameters.HMAC_SHA256_128BITTAG);
      result.put(
         "HMAC_SHA256_128BITTAG_RAW",
         HmacParameters.builder()
            .setKeySizeBytes(32)
            .setTagSizeBytes(16)
            .setVariant(HmacParameters.Variant.NO_PREFIX)
            .setHashType(HmacParameters.HashType.SHA256)
            .build()
      );
      result.put(
         "HMAC_SHA256_256BITTAG",
         HmacParameters.builder()
            .setKeySizeBytes(32)
            .setTagSizeBytes(32)
            .setVariant(HmacParameters.Variant.TINK)
            .setHashType(HmacParameters.HashType.SHA256)
            .build()
      );
      result.put(
         "HMAC_SHA256_256BITTAG_RAW",
         HmacParameters.builder()
            .setKeySizeBytes(32)
            .setTagSizeBytes(32)
            .setVariant(HmacParameters.Variant.NO_PREFIX)
            .setHashType(HmacParameters.HashType.SHA256)
            .build()
      );
      result.put(
         "HMAC_SHA512_128BITTAG",
         HmacParameters.builder()
            .setKeySizeBytes(64)
            .setTagSizeBytes(16)
            .setVariant(HmacParameters.Variant.TINK)
            .setHashType(HmacParameters.HashType.SHA512)
            .build()
      );
      result.put(
         "HMAC_SHA512_128BITTAG_RAW",
         HmacParameters.builder()
            .setKeySizeBytes(64)
            .setTagSizeBytes(16)
            .setVariant(HmacParameters.Variant.NO_PREFIX)
            .setHashType(HmacParameters.HashType.SHA512)
            .build()
      );
      result.put(
         "HMAC_SHA512_256BITTAG",
         HmacParameters.builder()
            .setKeySizeBytes(64)
            .setTagSizeBytes(32)
            .setVariant(HmacParameters.Variant.TINK)
            .setHashType(HmacParameters.HashType.SHA512)
            .build()
      );
      result.put(
         "HMAC_SHA512_256BITTAG_RAW",
         HmacParameters.builder()
            .setKeySizeBytes(64)
            .setTagSizeBytes(32)
            .setVariant(HmacParameters.Variant.NO_PREFIX)
            .setHashType(HmacParameters.HashType.SHA512)
            .build()
      );
      result.put("HMAC_SHA512_512BITTAG", PredefinedMacParameters.HMAC_SHA512_512BITTAG);
      result.put(
         "HMAC_SHA512_512BITTAG_RAW",
         HmacParameters.builder()
            .setKeySizeBytes(64)
            .setTagSizeBytes(64)
            .setVariant(HmacParameters.Variant.NO_PREFIX)
            .setHashType(HmacParameters.HashType.SHA512)
            .build()
      );
      return Collections.unmodifiableMap(result);
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use HMAC in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         HmacProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(CHUNKED_MAC_PRIMITIVE_CONSTRUCTOR);
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(MAC_PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, HmacParameters.class);
         MutableKeyDerivationRegistry.globalInstance().add(KEY_DERIVER, HmacParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyKeyManager, FIPS, newKeyAllowed);
      }
   }

   public static final KeyTemplate hmacSha256HalfDigestTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            HmacParameters.builder()
               .setKeySizeBytes(32)
               .setTagSizeBytes(16)
               .setHashType(HmacParameters.HashType.SHA256)
               .setVariant(HmacParameters.Variant.TINK)
               .build()
         )
      );
   }

   public static final KeyTemplate hmacSha256Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            HmacParameters.builder()
               .setKeySizeBytes(32)
               .setTagSizeBytes(32)
               .setHashType(HmacParameters.HashType.SHA256)
               .setVariant(HmacParameters.Variant.TINK)
               .build()
         )
      );
   }

   public static final KeyTemplate hmacSha512HalfDigestTemplate() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            HmacParameters.builder()
               .setKeySizeBytes(64)
               .setTagSizeBytes(32)
               .setHashType(HmacParameters.HashType.SHA512)
               .setVariant(HmacParameters.Variant.TINK)
               .build()
         )
      );
   }

   public static final KeyTemplate hmacSha512Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(
            HmacParameters.builder()
               .setKeySizeBytes(64)
               .setTagSizeBytes(64)
               .setHashType(HmacParameters.HashType.SHA512)
               .setVariant(HmacParameters.Variant.TINK)
               .build()
         )
      );
   }

   private HmacKeyManager() {
   }
}
