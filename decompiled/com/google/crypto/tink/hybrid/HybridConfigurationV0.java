package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.hybrid.internal.HpkeDecrypt;
import com.google.crypto.tink.hybrid.internal.HpkeEncrypt;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.subtle.EciesAeadHkdfHybridDecrypt;
import com.google.crypto.tink.subtle.EciesAeadHkdfHybridEncrypt;
import java.security.GeneralSecurityException;

class HybridConfigurationV0 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();

   private HybridConfigurationV0() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         HybridEncryptWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(EciesAeadHkdfHybridEncrypt::create, EciesPublicKey.class, HybridEncrypt.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(HpkeEncrypt::create, HpkePublicKey.class, HybridEncrypt.class));
         HybridDecryptWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(EciesAeadHkdfHybridDecrypt::create, EciesPrivateKey.class, HybridDecrypt.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(HpkeDecrypt::create, HpkePrivateKey.class, HybridDecrypt.class));
         return InternalConfiguration.createFromPrimitiveRegistry(builder.allowReparsingLegacyKeys().build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static Configuration get() throws GeneralSecurityException {
      if (TinkFipsUtil.useOnlyFips()) {
         throw new GeneralSecurityException("Cannot use non-FIPS-compliant HybridConfigurationV0 in FIPS mode");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }
}
