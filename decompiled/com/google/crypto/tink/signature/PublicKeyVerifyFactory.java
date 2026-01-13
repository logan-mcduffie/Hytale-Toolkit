package com.google.crypto.tink.signature;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.RegistryConfiguration;
import java.security.GeneralSecurityException;

@Deprecated
public final class PublicKeyVerifyFactory {
   @Deprecated
   public static PublicKeyVerify getPrimitive(KeysetHandle keysetHandle) throws GeneralSecurityException {
      PublicKeyVerifyWrapper.register();
      return keysetHandle.getPrimitive(RegistryConfiguration.get(), PublicKeyVerify.class);
   }

   private PublicKeyVerifyFactory() {
   }
}
