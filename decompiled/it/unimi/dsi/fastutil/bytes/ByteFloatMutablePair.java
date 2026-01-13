package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteFloatMutablePair implements ByteFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected byte left;
   protected float right;

   public ByteFloatMutablePair(byte left, float right) {
      this.left = left;
      this.right = right;
   }

   public static ByteFloatMutablePair of(byte left, float right) {
      return new ByteFloatMutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   public ByteFloatMutablePair left(byte l) {
      this.left = l;
      return this;
   }

   @Override
   public float rightFloat() {
      return this.right;
   }

   public ByteFloatMutablePair right(float r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteFloatPair) {
         return this.left == ((ByteFloatPair)other).leftByte() && this.right == ((ByteFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftByte() + "," + this.rightFloat() + ">";
   }
}
