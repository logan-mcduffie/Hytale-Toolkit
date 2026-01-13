package com.google.crypto.tink.keyderivation;

import java.security.GeneralSecurityException;

public final class KeysetDeriverWrapper {
   @Deprecated
   public static void register() throws GeneralSecurityException {
      com.google.crypto.tink.keyderivation.internal.KeysetDeriverWrapper.register();
   }

   private KeysetDeriverWrapper() {
   }
}
