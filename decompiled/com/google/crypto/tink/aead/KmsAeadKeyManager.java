package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.KmsClients;
import com.google.crypto.tink.aead.internal.LegacyFullAead;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyKeyManagerImpl;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KmsAeadKey;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

public final class KmsAeadKeyManager {
   private static final PrimitiveConstructor<LegacyKmsAeadKey, Aead> LEGACY_KMS_AEAD_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(
      KmsAeadKeyManager::create, LegacyKmsAeadKey.class, Aead.class
   );
   private static final KeyManager<Aead> legacyKeyManager = LegacyKeyManagerImpl.create(
      getKeyType(), Aead.class, KeyData.KeyMaterialType.REMOTE, KmsAeadKey.parser()
   );
   private static final KeyCreator<LegacyKmsAeadParameters> KEY_CREATOR = KmsAeadKeyManager::newKey;

   private static Aead create(LegacyKmsAeadKey key) throws GeneralSecurityException {
      Aead rawAead = KmsClients.get(key.getParameters().keyUri()).getAead(key.getParameters().keyUri());
      return LegacyFullAead.create(rawAead, key.getOutputPrefix());
   }

   @AccessesPartialKey
   private static LegacyKmsAeadKey newKey(LegacyKmsAeadParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
      return LegacyKmsAeadKey.create(parameters, idRequirement);
   }

   static String getKeyType() {
      return "type.googleapis.com/google.crypto.tink.KmsAeadKey";
   }

   public static void register(boolean newKeyAllowed) throws GeneralSecurityException {
      if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
         throw new GeneralSecurityException("Registering KMS AEAD is not supported in FIPS mode");
      } else {
         LegacyKmsAeadProtoSerialization.register();
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_KMS_AEAD_PRIMITIVE_CONSTRUCTOR);
         MutableKeyCreationRegistry.globalInstance().add(KEY_CREATOR, LegacyKmsAeadParameters.class);
         KeyManagerRegistry.globalInstance().registerKeyManager(legacyKeyManager, newKeyAllowed);
      }
   }

   public static KeyTemplate createKeyTemplate(String keyUri) {
      try {
         return KeyTemplate.createFrom(LegacyKmsAeadParameters.create(keyUri));
      } catch (GeneralSecurityException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   private KmsAeadKeyManager() {
   }
}
