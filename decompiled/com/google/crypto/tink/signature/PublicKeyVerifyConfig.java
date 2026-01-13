package com.google.crypto.tink.signature;

import java.security.GeneralSecurityException;

@Deprecated
public final class PublicKeyVerifyConfig {
   @Deprecated
   public static void registerStandardKeyTypes() throws GeneralSecurityException {
      SignatureConfig.register();
   }

   private PublicKeyVerifyConfig() {
   }
}
