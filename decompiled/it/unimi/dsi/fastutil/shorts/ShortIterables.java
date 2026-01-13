package it.unimi.dsi.fastutil.shorts;

public final class ShortIterables {
   private ShortIterables() {
   }

   public static long size(ShortIterable iterable) {
      long c = 0L;

      for (short dummy : iterable) {
         c++;
      }

      return c;
   }
}
