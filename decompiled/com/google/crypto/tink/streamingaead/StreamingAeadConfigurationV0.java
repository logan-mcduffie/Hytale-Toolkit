package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.subtle.AesCtrHmacStreaming;
import com.google.crypto.tink.subtle.AesGcmHkdfStreaming;
import java.security.GeneralSecurityException;

class StreamingAeadConfigurationV0 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();

   private StreamingAeadConfigurationV0() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         StreamingAeadWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AesGcmHkdfStreaming::create, AesGcmHkdfStreamingKey.class, StreamingAead.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AesCtrHmacStreaming::create, AesCtrHmacStreamingKey.class, StreamingAead.class));
         return InternalConfiguration.createFromPrimitiveRegistry(builder.allowReparsingLegacyKeys().build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static Configuration get() throws GeneralSecurityException {
      if (TinkFipsUtil.useOnlyFips()) {
         throw new GeneralSecurityException("Cannot use non-FIPS-compliant StreamingAead in FIPS mode");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }
}
