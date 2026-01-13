package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.daead.DeterministicAeadConfig;
import com.google.crypto.tink.hybrid.internal.HpkePrivateKeyManager;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;

public final class HybridConfig {
   public static final String ECIES_AEAD_HKDF_PUBLIC_KEY_TYPE_URL = initializeClassReturnInput("type.googleapis.com/google.crypto.tink.EciesAeadHkdfPublicKey");
   public static final String ECIES_AEAD_HKDF_PRIVATE_KEY_TYPE_URL = initializeClassReturnInput(
      "type.googleapis.com/google.crypto.tink.EciesAeadHkdfPrivateKey"
   );
   @Deprecated
   public static final RegistryConfig TINK_1_0_0 = RegistryConfig.getDefaultInstance();
   @Deprecated
   public static final RegistryConfig TINK_1_1_0 = RegistryConfig.getDefaultInstance();
   @Deprecated
   public static final RegistryConfig LATEST = RegistryConfig.getDefaultInstance();

   @CanIgnoreReturnValue
   private static String initializeClassReturnInput(String s) {
      return s;
   }

   @Deprecated
   public static void init() throws GeneralSecurityException {
      register();
   }

   public static void register() throws GeneralSecurityException {
      HybridDecryptWrapper.register();
      HybridEncryptWrapper.register();
      AeadConfig.register();
      DeterministicAeadConfig.register();
      if (!TinkFips.useOnlyFips()) {
         EciesAeadHkdfPrivateKeyManager.registerPair(true);
         HpkePrivateKeyManager.registerPair(true);
      }
   }

   private HybridConfig() {
   }

   static {
      try {
         init();
      } catch (GeneralSecurityException var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
