package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface ByteBigListIterator extends ByteBidirectionalIterator, BigListIterator<Byte> {
   default void set(byte k) {
      throw new UnsupportedOperationException();
   }

   default void add(byte k) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Byte k) {
      this.set(k.byteValue());
   }

   @Deprecated
   default void add(Byte k) {
      this.add(k.byteValue());
   }

   default long skip(long n) {
      long i = n;

      while (i-- != 0L && this.hasNext()) {
         this.nextByte();
      }

      return n - i - 1L;
   }

   default long back(long n) {
      long i = n;

      while (i-- != 0L && this.hasPrevious()) {
         this.previousByte();
      }

      return n - i - 1L;
   }

   @Override
   default int skip(int n) {
      return SafeMath.safeLongToInt(this.skip((long)n));
   }
}
