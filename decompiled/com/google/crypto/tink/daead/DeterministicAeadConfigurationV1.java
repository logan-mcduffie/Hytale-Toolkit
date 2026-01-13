package com.google.crypto.tink.daead;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.subtle.AesSiv;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

class DeterministicAeadConfigurationV1 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();
   private static final int KEY_SIZE_IN_BYTES = 64;

   private DeterministicAeadConfigurationV1() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         DeterministicAeadWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(
            PrimitiveConstructor.create(DeterministicAeadConfigurationV1::createAesSiv, AesSivKey.class, DeterministicAead.class)
         );
         return InternalConfiguration.createFromPrimitiveRegistry(builder.build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static Configuration get() throws GeneralSecurityException {
      if (TinkFipsUtil.useOnlyFips()) {
         throw new GeneralSecurityException("Cannot use non-FIPS-compliant DeterministicAeadConfigurationV1 in FIPS mode");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }

   private static DeterministicAead createAesSiv(AesSivKey key) throws GeneralSecurityException {
      if (key.getParameters().getKeySizeBytes() != 64) {
         throw new InvalidAlgorithmParameterException("invalid key size: " + key.getParameters().getKeySizeBytes() + ". Valid keys must have " + 64 + " bytes.");
      } else {
         return AesSiv.create(key);
      }
   }
}
