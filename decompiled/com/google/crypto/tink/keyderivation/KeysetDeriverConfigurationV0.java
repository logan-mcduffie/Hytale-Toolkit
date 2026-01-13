package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.keyderivation.internal.KeyDeriver;
import com.google.crypto.tink.keyderivation.internal.PrfBasedKeyDeriver;
import com.google.crypto.tink.prf.HkdfPrfKey;
import com.google.crypto.tink.subtle.prf.HkdfStreamingPrf;
import com.google.crypto.tink.subtle.prf.StreamingPrf;
import java.security.GeneralSecurityException;

class KeysetDeriverConfigurationV0 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();
   private static final PrimitiveRegistry PRF_REGISTRY = createPrfRegistry();

   private KeysetDeriverConfigurationV0() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         com.google.crypto.tink.keyderivation.internal.KeysetDeriverWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(
            PrimitiveConstructor.create(KeysetDeriverConfigurationV0::createHkdfPrfBasedKeyDeriver, PrfBasedKeyDerivationKey.class, KeyDeriver.class)
         );
         return InternalConfiguration.createFromPrimitiveRegistry(builder.allowReparsingLegacyKeys().build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   private static PrimitiveRegistry createPrfRegistry() {
      try {
         return PrimitiveRegistry.builder()
            .registerPrimitiveConstructor(PrimitiveConstructor.create(HkdfStreamingPrf::create, HkdfPrfKey.class, StreamingPrf.class))
            .build();
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static Configuration get() throws GeneralSecurityException {
      if (TinkFipsUtil.useOnlyFips()) {
         throw new GeneralSecurityException("Cannot use non-FIPS-compliant KeysetDeriverConfigurationV0 in FIPS mode");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }

   private static KeyDeriver createHkdfPrfBasedKeyDeriver(PrfBasedKeyDerivationKey key) throws GeneralSecurityException {
      KeyDeriver deriver = PrfBasedKeyDeriver.createWithPrfPrimitiveRegistry(PRF_REGISTRY, key);
      Object unused = deriver.deriveKey(new byte[]{1});
      return deriver;
   }
}
