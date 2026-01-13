package com.google.crypto.tink.aead;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.aead.internal.ChaCha20Poly1305Jce;
import com.google.crypto.tink.aead.internal.XAesGcm;
import com.google.crypto.tink.aead.internal.XChaCha20Poly1305Jce;
import com.google.crypto.tink.aead.subtle.AesGcmSiv;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.subtle.AesEaxJce;
import com.google.crypto.tink.subtle.AesGcmJce;
import com.google.crypto.tink.subtle.ChaCha20Poly1305;
import com.google.crypto.tink.subtle.EncryptThenAuthenticate;
import com.google.crypto.tink.subtle.XChaCha20Poly1305;
import java.security.GeneralSecurityException;

class AeadConfigurationV1 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();

   private AeadConfigurationV1() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         AeadWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(EncryptThenAuthenticate::create, AesCtrHmacAeadKey.class, Aead.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AesGcmJce::create, AesGcmKey.class, Aead.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AesGcmSiv::create, AesGcmSivKey.class, Aead.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AesEaxJce::create, AesEaxKey.class, Aead.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AeadConfigurationV1::createChaCha20Poly1305, ChaCha20Poly1305Key.class, Aead.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AeadConfigurationV1::createXChaCha20Poly1305, XChaCha20Poly1305Key.class, Aead.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(XAesGcm::create, XAesGcmKey.class, Aead.class));
         return InternalConfiguration.createFromPrimitiveRegistry(builder.build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static Configuration get() throws GeneralSecurityException {
      if (TinkFipsUtil.useOnlyFips()) {
         throw new GeneralSecurityException("Cannot use non-FIPS-compliant AeadConfigurationV1 in FIPS mode");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }

   private static Aead createChaCha20Poly1305(ChaCha20Poly1305Key key) throws GeneralSecurityException {
      return ChaCha20Poly1305Jce.isSupported() ? ChaCha20Poly1305Jce.create(key) : ChaCha20Poly1305.create(key);
   }

   private static Aead createXChaCha20Poly1305(XChaCha20Poly1305Key key) throws GeneralSecurityException {
      return XChaCha20Poly1305Jce.isSupported() ? XChaCha20Poly1305Jce.create(key) : XChaCha20Poly1305.create(key);
   }
}
