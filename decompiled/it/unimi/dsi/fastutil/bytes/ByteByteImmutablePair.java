package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteByteImmutablePair implements ByteBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final byte left;
   protected final byte right;

   public ByteByteImmutablePair(byte left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static ByteByteImmutablePair of(byte left, byte right) {
      return new ByteByteImmutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   @Override
   public byte rightByte() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteBytePair) {
         return this.left == ((ByteBytePair)other).leftByte() && this.right == ((ByteBytePair)other).rightByte();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftByte() + "," + this.rightByte() + ">";
   }
}
