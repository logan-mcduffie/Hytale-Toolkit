package com.google.common.flogger.util;

public class Checks {
   private Checks() {
   }

   public static <T> T checkNotNull(T value, String name) {
      if (value == null) {
         throw new NullPointerException(name + " must not be null");
      } else {
         return value;
      }
   }

   public static void checkArgument(boolean condition, String message) {
      if (!condition) {
         throw new IllegalArgumentException(message);
      }
   }

   public static void checkState(boolean condition, String message) {
      if (!condition) {
         throw new IllegalStateException(message);
      }
   }

   public static String checkMetadataIdentifier(String s) {
      if (s.isEmpty()) {
         throw new IllegalArgumentException("identifier must not be empty");
      } else if (!isLetter(s.charAt(0))) {
         throw new IllegalArgumentException("identifier must start with an ASCII letter: " + s);
      } else {
         for (int n = 1; n < s.length(); n++) {
            char c = s.charAt(n);
            if (!isLetter(c) && (c < '0' || c > '9') && c != '_') {
               throw new IllegalArgumentException("identifier must contain only ASCII letters, digits or underscore: " + s);
            }
         }

         return s;
      }
   }

   private static boolean isLetter(char c) {
      return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
   }
}
