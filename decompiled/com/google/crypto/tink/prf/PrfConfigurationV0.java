package com.google.crypto.tink.prf;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.subtle.PrfAesCmac;
import com.google.crypto.tink.subtle.PrfHmacJce;
import com.google.crypto.tink.subtle.prf.HkdfStreamingPrf;
import com.google.crypto.tink.subtle.prf.PrfImpl;
import java.security.GeneralSecurityException;

class PrfConfigurationV0 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();
   private static final int MIN_HKDF_PRF_KEY_SIZE = 32;

   private PrfConfigurationV0() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         PrfSetWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(PrfHmacJce::create, HmacPrfKey.class, Prf.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(PrfConfigurationV0::createHkdfPrf, HkdfPrfKey.class, Prf.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(PrfConfigurationV0::createAesCmacPrf, AesCmacPrfKey.class, Prf.class));
         return InternalConfiguration.createFromPrimitiveRegistry(builder.allowReparsingLegacyKeys().build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static Configuration get() throws GeneralSecurityException {
      if (TinkFipsUtil.useOnlyFips()) {
         throw new GeneralSecurityException("Cannot use non-FIPS-compliant PrfConfigurationV0 in FIPS mode");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }

   private static Prf createHkdfPrf(HkdfPrfKey key) throws GeneralSecurityException {
      if (key.getParameters().getKeySizeBytes() < 32) {
         throw new GeneralSecurityException("HkdfPrf key size must be at least 32");
      } else if (key.getParameters().getHashType() != HkdfPrfParameters.HashType.SHA256
         && key.getParameters().getHashType() != HkdfPrfParameters.HashType.SHA512) {
         throw new GeneralSecurityException("HkdfPrf hash type must be SHA256 or SHA512");
      } else {
         return PrfImpl.wrap(HkdfStreamingPrf.create(key));
      }
   }

   private static Prf createAesCmacPrf(AesCmacPrfKey key) throws GeneralSecurityException {
      if (key.getParameters().getKeySizeBytes() != 32) {
         throw new GeneralSecurityException("AesCmacPrf key size must be 32 bytes");
      } else {
         return PrfAesCmac.create(key);
      }
   }
}
