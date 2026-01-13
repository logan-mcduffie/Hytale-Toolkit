package it.unimi.dsi.fastutil.bytes;

public final class ByteIterables {
   private ByteIterables() {
   }

   public static long size(ByteIterable iterable) {
      long c = 0L;

      for (byte dummy : iterable) {
         c++;
      }

      return c;
   }
}
