package com.google.crypto.tink.jwt;

import com.google.crypto.tink.proto.RegistryConfig;
import java.security.GeneralSecurityException;

public final class JwtSignatureConfig {
   public static final String JWT_ECDSA_PUBLIC_KEY_TYPE_URL = JwtEcdsaVerifyKeyManager.getKeyType();
   public static final String JWT_ECDSA_PRIVATE_KEY_TYPE_URL = JwtEcdsaSignKeyManager.getKeyType();
   public static final String JWT_RSA_PKCS1_PRIVATE_KEY_TYPE_URL = JwtRsaSsaPkcs1SignKeyManager.getKeyType();
   public static final String JWT_RSA_PKCS1_PUBLIC_KEY_TYPE_URL = JwtRsaSsaPkcs1VerifyKeyManager.getKeyType();
   public static final String JWT_RSA_PSS_PRIVATE_KEY_TYPE_URL = JwtRsaSsaPssSignKeyManager.getKeyType();
   public static final String JWT_RSA_PSS_PUBLIC_KEY_TYPE_URL = JwtRsaSsaPssVerifyKeyManager.getKeyType();
   public static final RegistryConfig LATEST = RegistryConfig.getDefaultInstance();

   public static void register() throws GeneralSecurityException {
      JwtPublicKeySignWrapper.register();
      JwtPublicKeyVerifyWrapper.register();
      JwtEcdsaSignKeyManager.registerPair(true);
      JwtRsaSsaPkcs1SignKeyManager.registerPair(true);
      JwtRsaSsaPssSignKeyManager.registerPair(true);
   }

   private JwtSignatureConfig() {
   }
}
