package com.nimbusds.jose.crypto.utils;

public class ConstantTimeUtils {
   public static boolean areEqual(byte[] a, byte[] b) {
      if (a.length != b.length) {
         return false;
      } else {
         int result = 0;

         for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
         }

         return result == 0;
      }
   }

   private ConstantTimeUtils() {
   }
}
