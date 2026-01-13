package com.google.crypto.tink.prf;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
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
import com.google.crypto.tink.prf.internal.HmacPrfProtoSerialization;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.subtle.PrfHmacJce;
import com.google.crypto.tink.util.SecretBytes;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class HmacPrfKeyManager {
   private static final PrimitiveConstructor<HmacPrfKey, Prf> PRF_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      PrfHmacJce::create, HmacPrfKey.class, Prf.class
   );
   private static final KeyManager<Prf> legacyKeyManager = LegacyKeyManagerImpl.create(
      getKeyType(), Prf.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.HmacPrfKey.parser()
   );
   private static final KeyCreator<HmacPrfParameters> KEY_CREATOR = HmacPrfKeyManager::newKey;
   private static final MutableKeyDerivationRegistry.InsecureKeyCreator<HmacPrfParameters> KEY_DERIVER = HmacPrfKeyManager::createHmacKeyFromRandomness;
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;

   @AccessesPartialKey
   private static HmacPrfKey newKey(HmacPrfParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      if (idRequirement != null) {
         throw new GeneralSecurityException("Id Requirement is not supported for HMAC PRF keys");
      } else {
         return HmacPrfKey.builder().setParameters(parameters).setKeyBytes(SecretBytes.randomBytes(parameters.getKeySizeBytes())).build();
      }
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.HmacPrfKey";
   }

   @AccessesPartialKey
   static HmacPrfKey createHmacKeyFromRandomness(HmacPrfParameters parameters, InputStream stream, @Nullable Integer idRequirement, SecretKeyAccess access) throws GeneralSecurityException {
      return HmacPrfKey.builder().setParameters(parameters).setKeyBytes(Util.readIntoSecretBytes(stream, parameters.getKeySizeBytes(), access)).build();
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("HMAC_SHA256_PRF", PredefinedPrfParameters.HMAC_SHA256_PRF);
      result.put("HMAC_SHA512_PRF", PredefinedPrfParameters.HMAC_SHA512_PRF);
      return Collections.unmodifiableMap(result);
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use HMAC in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         HmacPrfProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(PRF_PRIMITIVE_CONSTRUCTOR);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, HmacPrfParameters.class);
         MutableKeyDerivationRegistry.globalInstance().add(KEY_DERIVER, HmacPrfParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManagerWithFipsCompatibility(legacyKeyManager, FIPS, newKeyAllowed);
      }
   }

   public static final KeyTemplate hmacSha256Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(HmacPrfParameters.builder().setKeySizeBytes(32).setHashType(HmacPrfParameters.HashType.SHA256).build())
      );
   }

   public static final KeyTemplate hmacSha512Template() {
      return TinkBugException.exceptionIsBug(
         () -> KeyTemplate.createFrom(HmacPrfParameters.builder().setKeySizeBytes(64).setHashType(HmacPrfParameters.HashType.SHA512).build())
      );
   }

   private HmacPrfKeyManager() {
   }
}
