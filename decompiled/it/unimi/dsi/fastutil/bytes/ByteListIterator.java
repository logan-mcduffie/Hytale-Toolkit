package it.unimi.dsi.fastutil.bytes;

import java.util.ListIterator;

public interface ByteListIterator extends ByteBidirectionalIterator, ListIterator<Byte> {
   default void set(byte k) {
      throw new UnsupportedOperationException();
   }

   default void add(byte k) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void remove() {
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

   @Deprecated
   @Override
   default Byte next() {
      return ByteBidirectionalIterator.super.next();
   }

   @Deprecated
   @Override
   default Byte previous() {
      return ByteBidirectionalIterator.super.previous();
   }
}
