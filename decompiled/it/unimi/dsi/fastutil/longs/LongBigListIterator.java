package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface LongBigListIterator extends LongBidirectionalIterator, BigListIterator<Long> {
   default void set(long k) {
      throw new UnsupportedOperationException();
   }

   default void add(long k) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Long k) {
      this.set(k.longValue());
   }

   @Deprecated
   default void add(Long k) {
      this.add(k.longValue());
   }

   default long skip(long n) {
      long i = n;

      while (i-- != 0L && this.hasNext()) {
         this.nextLong();
      }

      return n - i - 1L;
   }

   default long back(long n) {
      long i = n;

      while (i-- != 0L && this.hasPrevious()) {
         this.previousLong();
      }

      return n - i - 1L;
   }

   @Override
   default int skip(int n) {
      return SafeMath.safeLongToInt(this.skip((long)n));
   }
}
