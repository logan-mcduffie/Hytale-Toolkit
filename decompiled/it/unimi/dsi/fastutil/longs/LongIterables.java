package it.unimi.dsi.fastutil.longs;

public final class LongIterables {
   private LongIterables() {
   }

   public static long size(LongIterable iterable) {
      long c = 0L;

      for (long dummy : iterable) {
         c++;
      }

      return c;
   }
}
