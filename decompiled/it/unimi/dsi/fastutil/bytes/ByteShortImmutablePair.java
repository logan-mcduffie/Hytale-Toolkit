package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteShortImmutablePair implements ByteShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final byte left;
   protected final short right;

   public ByteShortImmutablePair(byte left, short right) {
      this.left = left;
      this.right = right;
   }

   public static ByteShortImmutablePair of(byte left, short right) {
      return new ByteShortImmutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteShortPair) {
         return this.left == ((ByteShortPair)other).leftByte() && this.right == ((ByteShortPair)other).rightShort();
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
      return "<" + this.leftByte() + "," + this.rightShort() + ">";
   }
}
