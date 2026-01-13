package com.google.crypto.tink.mac;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.RegistryConfiguration;
import java.security.GeneralSecurityException;

@Deprecated
public final class MacFactory {
   @Deprecated
   public static Mac getPrimitive(KeysetHandle keysetHandle) throws GeneralSecurityException {
      MacWrapper.register();
      return keysetHandle.getPrimitive(RegistryConfiguration.get(), Mac.class);
   }

   private MacFactory() {
   }
}
