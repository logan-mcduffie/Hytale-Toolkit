package it.unimi.dsi.fastutil.doubles;

public final class DoubleIterables {
   private DoubleIterables() {
   }

   public static long size(DoubleIterable iterable) {
      long c = 0L;

      for (double dummy : iterable) {
         c++;
      }

      return c;
   }
}
