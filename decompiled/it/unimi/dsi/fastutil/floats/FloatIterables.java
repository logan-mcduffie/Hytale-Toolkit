package it.unimi.dsi.fastutil.floats;

public final class FloatIterables {
   private FloatIterables() {
   }

   public static long size(FloatIterable iterable) {
      long c = 0L;

      for (float dummy : iterable) {
         c++;
      }

      return c;
   }
}
