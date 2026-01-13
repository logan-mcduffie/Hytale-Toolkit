package it.unimi.dsi.fastutil.booleans;

public final class BooleanIterables {
   private BooleanIterables() {
   }

   public static long size(BooleanIterable iterable) {
      long c = 0L;

      for (boolean dummy : iterable) {
         c++;
      }

      return c;
   }
}
