package it.unimi.dsi.fastutil.chars;

public final class CharIterables {
   private CharIterables() {
   }

   public static long size(CharIterable iterable) {
      long c = 0L;

      for (char dummy : iterable) {
         c++;
      }

      return c;
   }
}
