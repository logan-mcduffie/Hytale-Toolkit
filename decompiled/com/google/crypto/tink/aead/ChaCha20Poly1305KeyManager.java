package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.aead.internal.ChaCha20Poly1305Jce;
import com.google.crypto.tink.aead.internal.ChaCha20Poly1305ProtoSerialization;
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
import com.google.crypto.tink.subtle.ChaCha20Poly1305;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ChaCha20Poly1305KeyManager {
   private static final PrimitiveConstructor<ChaCha20Poly1305Key, Aead> CHA_CHA_20_POLY_1305_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      ChaCha20Poly1305KeyManager::createAead, ChaCha20Poly1305Key.class, Aead.class
   );
   private static final int KEY_SIZE_IN_BYTES = 32;
   private static final KeyCreator<ChaCha20Poly1305Parameters> KEY_CREATOR = ChaCha20Poly1305KeyManager::createChaChaKey;
   private static final KeyManager<Aead> legacyKeyManager = LegacyKeyManagerImpl.create(
      getKeyType(), Aead.class, KeyData.KeyMaterialType.SYMMETRIC, com.google.crypto.tink.proto.ChaCha20Poly1305Key.parser()
   );

   private static Aead createAead(ChaCha20Poly1305Key key) throws GeneralSecurityException {
      return ChaCha20Poly1305Jce.isSupported() ? ChaCha20Poly1305Jce.create(key) : ChaCha20Poly1305.create(key);
   }

   @AccessesPartialKey
   static ChaCha20Poly1305Key createChaChaKey(ChaCha20Poly1305Parameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      return ChaCha20Poly1305Key.create(parameters.getVariant(), SecretBytes.randomBytes(32), idRequirement);
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.ChaCha20Poly1305Key";
   }

   private static Map<String, Parameters> namedParameters() throws GeneralSecurityException {
      Map<String, Parameters> result = new HashMap<>();
      result.put("CHACHA20_POLY1305", ChaCha20Poly1305Parameters.create(ChaCha20Poly1305Parameters.Variant.TINK));
      result.put("CHACHA20_POLY1305_RAW", ChaCha20Poly1305Parameters.create(ChaCha20Poly1305Parameters.Variant.NO_PREFIX));
      return Collections.unmodifiableMap(result);
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering ChaCha20Poly1305 is not supported in FIPS mode");
      } else {
         ChaCha20Poly1305ProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(CHA_CHA_20_POLY_1305_PRIMITIVE_CONSTRUCTOR);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, ChaCha20Poly1305Parameters.class);
         MutableParametersRegistry.globalInstance().putAll(namedParameters());
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyKeyManager, newKeyAllowed);
      }
   }

   public static final KeyTemplate chaCha20Poly1305Template() {
      return TinkBugException.exceptionIsBug(() -> KeyTemplate.createFrom(ChaCha20Poly1305Parameters.create(ChaCha20Poly1305Parameters.Variant.TINK)));
   }

   public static final KeyTemplate rawChaCha20Poly1305Template() {
      return TinkBugException.exceptionIsBug(() -> KeyTemplate.createFrom(ChaCha20Poly1305Parameters.create(ChaCha20Poly1305Parameters.Variant.NO_PREFIX)));
   }

   private ChaCha20Poly1305KeyManager() {
   }
}
