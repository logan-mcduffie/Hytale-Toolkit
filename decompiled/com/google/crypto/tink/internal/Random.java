package com.google.crypto.tink.internal;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;

public final class Random {
   private static final ThreadLocal<SecureRandom> localRandom = new ThreadLocal<SecureRandom>() {
      protected SecureRandom initialValue() {
         return Random.newDefaultSecureRandom();
      }
   };

   private static SecureRandom create() {
      Provider conscryptProvider = ConscryptUtil.providerOrNull();
      if (conscryptProvider != null) {
         try {
            return SecureRandom.getInstance("SHA1PRNG", conscryptProvider);
         } catch (GeneralSecurityException var4) {
         }
      }

      Provider conscryptProviderWithReflection = ConscryptUtil.providerWithReflectionOrNull();
      if (conscryptProviderWithReflection != null) {
         try {
            return SecureRandom.getInstance("SHA1PRNG", conscryptProviderWithReflection);
         } catch (GeneralSecurityException var3) {
         }
      }

      return new SecureRandom();
   }

   private static SecureRandom newDefaultSecureRandom() {
      SecureRandom retval = create();
      retval.nextLong();
      return retval;
   }

   public static byte[] randBytes(int size) {
      byte[] rand = new byte[size];
      localRandom.get().nextBytes(rand);
      return rand;
   }

   public static final int randInt(int max) {
      return localRandom.get().nextInt(max);
   }

   public static final int randInt() {
      return localRandom.get().nextInt();
   }

   public static final void validateUsesConscrypt() throws GeneralSecurityException {
      if (!ConscryptUtil.isConscryptProvider(localRandom.get().getProvider())) {
         throw new GeneralSecurityException(
            "Requires GmsCore_OpenSSL, AndroidOpenSSL or Conscrypt to generate randomness, but got " + localRandom.get().getProvider().getName()
         );
      }
   }

   private Random() {
   }
}
