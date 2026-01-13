package com.google.crypto.tink.config;

import com.google.crypto.tink.daead.DeterministicAeadConfig;
import com.google.crypto.tink.hybrid.HybridConfig;
import com.google.crypto.tink.prf.PrfConfig;
import com.google.crypto.tink.proto.RegistryConfig;
import com.google.crypto.tink.signature.SignatureConfig;
import com.google.crypto.tink.streamingaead.StreamingAeadConfig;
import java.security.GeneralSecurityException;

public final class TinkConfig {
   @Deprecated
   public static final RegistryConfig TINK_1_0_0 = RegistryConfig.getDefaultInstance();
   @Deprecated
   public static final RegistryConfig TINK_1_1_0 = RegistryConfig.getDefaultInstance();
   @Deprecated
   public static final RegistryConfig LATEST = RegistryConfig.getDefaultInstance();

   @Deprecated
   public static void init() throws GeneralSecurityException {
      register();
   }

   public static void register() throws GeneralSecurityException {
      DeterministicAeadConfig.register();
      HybridConfig.register();
      PrfConfig.register();
      SignatureConfig.register();
      StreamingAeadConfig.register();
   }

   private TinkConfig() {
   }

   static {
      try {
         init();
      } catch (GeneralSecurityException var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
