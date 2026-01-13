package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteShortMutablePair implements ByteShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected byte left;
   protected short right;

   public ByteShortMutablePair(byte left, short right) {
      this.left = left;
      this.right = right;
   }

   public static ByteShortMutablePair of(byte left, short right) {
      return new ByteShortMutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   public ByteShortMutablePair left(byte l) {
      this.left = l;
      return this;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   public ByteShortMutablePair right(short r) {
      this.right = r;
      return this;
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
