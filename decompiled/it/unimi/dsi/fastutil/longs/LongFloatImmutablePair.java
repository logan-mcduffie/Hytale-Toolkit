package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongFloatImmutablePair implements LongFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final long left;
   protected final float right;

   public LongFloatImmutablePair(long left, float right) {
      this.left = left;
      this.right = right;
   }

   public static LongFloatImmutablePair of(long left, float right) {
      return new LongFloatImmutablePair(left, right);
   }

   @Override
   public long leftLong() {
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
      } else if (other instanceof LongFloatPair) {
         return this.left == ((LongFloatPair)other).leftLong() && this.right == ((LongFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.long2int(this.left) * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftLong() + "," + this.rightFloat() + ">";
   }
}
