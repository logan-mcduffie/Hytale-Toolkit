package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface BooleanBigListIterator extends BooleanBidirectionalIterator, BigListIterator<Boolean> {
   default void set(boolean k) {
      throw new UnsupportedOperationException();
   }

   default void add(boolean k) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Boolean k) {
      this.set(k.booleanValue());
   }

   @Deprecated
   default void add(Boolean k) {
      this.add(k.booleanValue());
   }

   default long skip(long n) {
      long i = n;

      while (i-- != 0L && this.hasNext()) {
         this.nextBoolean();
      }

      return n - i - 1L;
   }

   default long back(long n) {
      long i = n;

      while (i-- != 0L && this.hasPrevious()) {
         this.previousBoolean();
      }

      return n - i - 1L;
   }

   @Override
   default int skip(int n) {
      return SafeMath.safeLongToInt(this.skip((long)n));
   }
}
