package com.nimbusds.jose.crypto.bc;

import java.security.Provider;
import java.security.Security;

public final class BouncyCastleFIPSProviderSingleton {
   private static Provider bouncyCastleFIPSProvider;

   private BouncyCastleFIPSProviderSingleton() {
   }

   public static Provider getInstance() {
      if (bouncyCastleFIPSProvider == null) {
         bouncyCastleFIPSProvider = Security.getProvider("BCFIPS");
      }

      return bouncyCastleFIPSProvider;
   }
}
