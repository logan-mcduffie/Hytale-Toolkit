package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface ShortBigListIterator extends ShortBidirectionalIterator, BigListIterator<Short> {
   default void set(short k) {
      throw new UnsupportedOperationException();
   }

   default void add(short k) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Short k) {
      this.set(k.shortValue());
   }

   @Deprecated
   default void add(Short k) {
      this.add(k.shortValue());
   }

   default long skip(long n) {
      long i = n;

      while (i-- != 0L && this.hasNext()) {
         this.nextShort();
      }

      return n - i - 1L;
   }

   default long back(long n) {
      long i = n;

      while (i-- != 0L && this.hasPrevious()) {
         this.previousShort();
      }

      return n - i - 1L;
   }

   @Override
   default int skip(int n) {
      return SafeMath.safeLongToInt(this.skip((long)n));
   }
}
