package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.config.TinkFips;
import com.google.crypto.tink.keyderivation.internal.PrfBasedDeriverKeyManager;
import com.google.crypto.tink.prf.HkdfPrfKeyManager;
import java.security.GeneralSecurityException;

public final class KeyDerivationConfig {
   public static void register() throws GeneralSecurityException {
      com.google.crypto.tink.keyderivation.internal.KeysetDeriverWrapper.register();
      if (!TinkFips.useOnlyFips()) {
         HkdfPrfKeyManager.register(true);
         PrfBasedDeriverKeyManager.register(true);
      }
   }

   private KeyDerivationConfig() {
   }
}
