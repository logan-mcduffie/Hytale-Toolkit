package com.nimbusds.jose.crypto.opts;

import com.nimbusds.jose.Option;
import com.nimbusds.jose.crypto.impl.RSAKeyUtils;
import java.security.PrivateKey;
import java.util.Set;

public class OptionUtils {
   @Deprecated
   public static <T extends Option> boolean optionIsPresent(Set<? extends Option> opts, Class<T> tClass) {
      if (opts != null && !opts.isEmpty()) {
         for (Option o : opts) {
            if (o.getClass().isAssignableFrom(tClass)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static void ensureMinRSAPrivateKeySize(PrivateKey privateKey, Set<? extends Option> opts) {
      if (!opts.contains(AllowWeakRSAKey.getInstance())) {
         int keyBitLength = RSAKeyUtils.keyBitLength(privateKey);
         if (keyBitLength > 0 && keyBitLength < 2048) {
            throw new IllegalArgumentException("The RSA key size must be at least 2048 bits");
         }
      }
   }
}
