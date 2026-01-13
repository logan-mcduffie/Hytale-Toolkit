package com.google.crypto.tink.mac;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;

public final class MacConfig {
   public static final String HMAC_TYPE_URL = initializeClassReturnInput("type.googleapis.com/google.crypto.tink.HmacKey");
   @Deprecated
   public static final RegistryConfig TINK_1_0_0 = RegistryConfig.getDefaultInstance();
   @Deprecated
   public static final RegistryConfig TINK_1_1_0 = TINK_1_0_0;
   @Deprecated
   public static final RegistryConfig LATEST = TINK_1_0_0;

   @CanIgnoreReturnValue
   private static String initializeClassReturnInput(String s) {
      return s;
   }

   @Deprecated
   public static void init() throws GeneralSecurityException {
      register();
   }

   public static void register() throws GeneralSecurityException {
      MacWrapper.register();
      ChunkedMacWrapper.register();
      HmacKeyManager.register(true);
      if (!TinkFips.useOnlyFips()) {
         AesCmacKeyManager.register(true);
      }
   }

   @Deprecated
   public static void registerStandardKeyTypes() throws GeneralSecurityException {
      register();
   }

   private MacConfig() {
   }

   static {
      try {
         init();
      } catch (GeneralSecurityException var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
