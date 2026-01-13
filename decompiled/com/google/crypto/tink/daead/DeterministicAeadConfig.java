package com.google.crypto.tink.daead;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import java.security.GeneralSecurityException;

public final class DeterministicAeadConfig {
   public static final String AES_SIV_TYPE_URL = initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesSivKey");
   @Deprecated
   public static final RegistryConfig TINK_1_1_0 = RegistryConfig.getDefaultInstance();
   @Deprecated
   public static final RegistryConfig LATEST = RegistryConfig.getDefaultInstance();

   @CanIgnoreReturnValue
   private static String initializeClassReturnInput(String s) {
      return s;
   }

   @Deprecated
   @InlineMe(replacement = "DeterministicAeadConfig.register()", imports = "com.google.crypto.tink.daead.DeterministicAeadConfig")
   public static void init() throws GeneralSecurityException {
      register();
   }

   public static void register() throws GeneralSecurityException {
      DeterministicAeadWrapper.register();
      if (!TinkFips.useOnlyFips()) {
         AesSivKeyManager.register(true);
      }
   }

   private DeterministicAeadConfig() {
   }

   static {
      try {
         register();
      } catch (GeneralSecurityException var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
