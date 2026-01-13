package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface CharBigListIterator extends CharBidirectionalIterator, BigListIterator<Character> {
   default void set(char k) {
      throw new UnsupportedOperationException();
   }

   default void add(char k) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Character k) {
      this.set(k.charValue());
   }

   @Deprecated
   default void add(Character k) {
      this.add(k.charValue());
   }

   default long skip(long n) {
      long i = n;

      while (i-- != 0L && this.hasNext()) {
         this.nextChar();
      }

      return n - i - 1L;
   }

   default long back(long n) {
      long i = n;

      while (i-- != 0L && this.hasPrevious()) {
         this.previousChar();
      }

      return n - i - 1L;
   }

   @Override
   default int skip(int n) {
      return SafeMath.safeLongToInt(this.skip((long)n));
   }
}
