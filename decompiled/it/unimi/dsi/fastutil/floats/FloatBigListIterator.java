package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface FloatBigListIterator extends FloatBidirectionalIterator, BigListIterator<Float> {
   default void set(float k) {
      throw new UnsupportedOperationException();
   }

   default void add(float k) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Float k) {
      this.set(k.floatValue());
   }

   @Deprecated
   default void add(Float k) {
      this.add(k.floatValue());
   }

   default long skip(long n) {
      long i = n;

      while (i-- != 0L && this.hasNext()) {
         this.nextFloat();
      }

      return n - i - 1L;
   }

   default long back(long n) {
      long i = n;

      while (i-- != 0L && this.hasPrevious()) {
         this.previousFloat();
      }

      return n - i - 1L;
   }

   @Override
   default int skip(int n) {
      return SafeMath.safeLongToInt(this.skip((long)n));
   }
}
