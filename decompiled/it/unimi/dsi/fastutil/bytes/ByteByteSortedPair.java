package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;

public interface ByteByteSortedPair extends ByteBytePair, SortedPair<Byte>, Serializable {
   static ByteByteSortedPair of(byte left, byte right) {
      return ByteByteImmutableSortedPair.of(left, right);
   }

   default boolean contains(byte e) {
      return e == this.leftByte() || e == this.rightByte();
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return o == null ? false : this.contains(((Byte)o).byteValue());
   }
}
