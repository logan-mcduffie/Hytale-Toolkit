package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface ByteBidirectionalIterator extends ByteIterator, ObjectBidirectionalIterator<Byte> {
   byte previousByte();

   @Deprecated
   default Byte previous() {
      return this.previousByte();
   }

   @Override
   default int back(int n) {
      int i = n;

      while (i-- != 0 && this.hasPrevious()) {
         this.previousByte();
      }

      return n - i - 1;
   }

   @Override
   default int skip(int n) {
      return ByteIterator.super.skip(n);
   }
}
