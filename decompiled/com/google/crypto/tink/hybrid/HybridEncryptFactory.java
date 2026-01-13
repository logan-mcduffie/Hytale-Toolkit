package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.RegistryConfiguration;
import java.security.GeneralSecurityException;

@Deprecated
public final class HybridEncryptFactory {
   @Deprecated
   public static HybridEncrypt getPrimitive(KeysetHandle keysetHandle) throws GeneralSecurityException {
      HybridEncryptWrapper.register();
      return keysetHandle.getPrimitive(RegistryConfiguration.get(), HybridEncrypt.class);
   }

   private HybridEncryptFactory() {
   }
}
