package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;

public final class StreamingAeadConfig {
   public static final String AES_CTR_HMAC_STREAMINGAEAD_TYPE_URL = initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey");
   public static final String AES_GCM_HKDF_STREAMINGAEAD_TYPE_URL = initializeClassReturnInput("type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey");
   @Deprecated
   public static final RegistryConfig TINK_1_1_0 = RegistryConfig.getDefaultInstance();
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
      StreamingAeadWrapper.register();
      if (!TinkFips.useOnlyFips()) {
         AesCtrHmacStreamingKeyManager.register(true);
         AesGcmHkdfStreamingKeyManager.register(true);
      }
   }

   private StreamingAeadConfig() {
   }

   static {
      try {
         init();
      } catch (GeneralSecurityException var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
