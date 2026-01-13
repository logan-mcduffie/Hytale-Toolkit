package com.google.crypto.tink.subtle;

public final class Hex {
   public static String encode(final byte[] bytes) {
      String chars = "0123456789abcdef";
      StringBuilder result = new StringBuilder(2 * bytes.length);

      for (byte b : bytes) {
         int val = b & 255;
         result.append(chars.charAt(val / 16));
         result.append(chars.charAt(val % 16));
      }

      return result.toString();
   }

   public static byte[] decode(String hex) {
      if (hex.length() % 2 != 0) {
         throw new IllegalArgumentException("Expected a string of even length");
      } else {
         int size = hex.length() / 2;
         byte[] result = new byte[size];

         for (int i = 0; i < size; i++) {
            int hi = Character.digit(hex.charAt(2 * i), 16);
            int lo = Character.digit(hex.charAt(2 * i + 1), 16);
            if (hi == -1 || lo == -1) {
               throw new IllegalArgumentException("input is not hexadecimal");
            }

            result[i] = (byte)(16 * hi + lo);
         }

         return result;
      }
   }

   private Hex() {
   }
}
