package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;
import java.util.Objects;

public class ByteByteImmutableSortedPair extends ByteByteImmutablePair implements ByteByteSortedPair, Serializable {
   private static final long serialVersionUID = 0L;

   private ByteByteImmutableSortedPair(byte left, byte right) {
      super(left, right);
   }

   public static ByteByteImmutableSortedPair of(byte left, byte right) {
      return left <= right ? new ByteByteImmutableSortedPair(left, right) : new ByteByteImmutableSortedPair(right, left);
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteByteSortedPair) {
         return this.left == ((ByteByteSortedPair)other).leftByte() && this.right == ((ByteByteSortedPair)other).rightByte();
      } else {
         return !(other instanceof SortedPair)
            ? false
            : Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
      }
   }

   @Override
   public String toString() {
      return "{" + this.leftByte() + "," + this.rightByte() + "}";
   }
}
