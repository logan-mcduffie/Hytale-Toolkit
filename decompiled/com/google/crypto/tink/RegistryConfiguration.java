package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public class RegistryConfiguration {
   private RegistryConfiguration() {
   }

   public static Configuration get() throws GeneralSecurityException {
      return com.google.crypto.tink.internal.RegistryConfiguration.get();
   }
}
