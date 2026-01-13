package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteFloatImmutablePair implements ByteFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final byte left;
   protected final float right;

   public ByteFloatImmutablePair(byte left, float right) {
      this.left = left;
      this.right = right;
   }

   public static ByteFloatImmutablePair of(byte left, float right) {
      return new ByteFloatImmutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   @Override
   public float rightFloat() {
      return this.right;
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
