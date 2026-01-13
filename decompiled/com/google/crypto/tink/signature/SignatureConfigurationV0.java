package com.google.crypto.tink.signature;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.subtle.EcdsaSignJce;
import com.google.crypto.tink.subtle.EcdsaVerifyJce;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.Ed25519Verify;
import com.google.crypto.tink.subtle.RsaSsaPkcs1SignJce;
import com.google.crypto.tink.subtle.RsaSsaPkcs1VerifyJce;
import com.google.crypto.tink.subtle.RsaSsaPssSignJce;
import com.google.crypto.tink.subtle.RsaSsaPssVerifyJce;
import java.security.GeneralSecurityException;

class SignatureConfigurationV0 {
   private static final InternalConfiguration INTERNAL_CONFIGURATION = create();

   private SignatureConfigurationV0() {
   }

   private static InternalConfiguration create() {
      try {
         PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
         PublicKeySignWrapper.registerToInternalPrimitiveRegistry(builder);
         PublicKeyVerifyWrapper.registerToInternalPrimitiveRegistry(builder);
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(EcdsaSignJce::create, EcdsaPrivateKey.class, PublicKeySign.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(EcdsaVerifyJce::create, EcdsaPublicKey.class, PublicKeyVerify.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(RsaSsaPssSignJce::create, RsaSsaPssPrivateKey.class, PublicKeySign.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(RsaSsaPssVerifyJce::create, RsaSsaPssPublicKey.class, PublicKeyVerify.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(RsaSsaPkcs1SignJce::create, RsaSsaPkcs1PrivateKey.class, PublicKeySign.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(RsaSsaPkcs1VerifyJce::create, RsaSsaPkcs1PublicKey.class, PublicKeyVerify.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(Ed25519Sign::create, Ed25519PrivateKey.class, PublicKeySign.class));
         builder.registerPrimitiveConstructor(PrimitiveConstructor.create(Ed25519Verify::create, Ed25519PublicKey.class, PublicKeyVerify.class));
         return InternalConfiguration.createFromPrimitiveRegistry(builder.allowReparsingLegacyKeys().build());
      } catch (GeneralSecurityException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static Configuration get() throws GeneralSecurityException {
      if (TinkFipsUtil.useOnlyFips()) {
         throw new GeneralSecurityException("Cannot use non-FIPS-compliant SignatureConfigurationV0 in FIPS mode");
      } else {
         return INTERNAL_CONFIGURATION;
      }
   }
}
