package it.unimi.dsi.fastutil.ints;

public final class IntIterables {
   private IntIterables() {
   }

   public static long size(IntIterable iterable) {
      long c = 0L;

      for (int dummy : iterable) {
         c++;
      }

      return c;
   }
}
