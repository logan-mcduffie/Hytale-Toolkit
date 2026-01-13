package com.google.crypto.tink.mac;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.mac.internal.ChunkedAesCmacImpl;
import com.google.crypto.tink.mac.internal.ChunkedHmacImpl;
import com.google.crypto.tink.subtle.PrfMac;
import java.security.GeneralSecurityException;

class MacConfigurationV0 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();
   private static final int AES_CMAC_KEY_SIZE_BYTES = 32;

   private MacConfigurationV0() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         MacWrapper.registerToInternalPrimitiveRegistry(builder);
         ChunkedMacWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(MacConfigurationV0::createAesCmac, AesCmacKey.class, Mac.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(PrfMac::create, HmacKey.class, Mac.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(MacConfigurationV0::createChunkedAesCmac, AesCmacKey.class, ChunkedMac.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(ChunkedHmacImpl::new, HmacKey.class, ChunkedMac.class));
         return InternalConfiguration.createFromPrimitiveRegistry(builder.allowReparsingLegacyKeys().build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static Configuration get() throws GeneralSecurityException {
      if (TinkFipsUtil.useOnlyFips()) {
         throw new GeneralSecurityException("Cannot use non-FIPS-compliant MacConfigurationV0 in FIPS mode");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }

   private static ChunkedMac createChunkedAesCmac(AesCmacKey key) throws GeneralSecurityException {
      if (key.getParameters().getKeySizeBytes() != 32) {
         throw new GeneralSecurityException("AesCmac key size is not 32 bytes");
      } else {
         return ChunkedAesCmacImpl.create(key);
      }
   }

   private static Mac createAesCmac(AesCmacKey key) throws GeneralSecurityException {
      if (key.getParameters().getKeySizeBytes() != 32) {
         throw new GeneralSecurityException("AesCmac key size is not 32 bytes");
      } else {
         return PrfMac.create(key);
      }
   }
}
