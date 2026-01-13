package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface IntBigListIterator extends IntBidirectionalIterator, BigListIterator<Integer> {
   default void set(int k) {
      throw new UnsupportedOperationException();
   }

   default void add(int k) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Integer k) {
      this.set(k.intValue());
   }

   @Deprecated
   default void add(Integer k) {
      this.add(k.intValue());
   }

   default long skip(long n) {
      long i = n;

      while (i-- != 0L && this.hasNext()) {
         this.nextInt();
      }

      return n - i - 1L;
   }

   default long back(long n) {
      long i = n;

      while (i-- != 0L && this.hasPrevious()) {
         this.previousInt();
      }

      return n - i - 1L;
   }

   @Override
   default int skip(int n) {
      return SafeMath.safeLongToInt(this.skip((long)n));
   }
}
