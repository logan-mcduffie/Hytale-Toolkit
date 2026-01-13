package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntFloatImmutablePair implements IntFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final int left;
   protected final float right;

   public IntFloatImmutablePair(int left, float right) {
      this.left = left;
      this.right = right;
   }

   public static IntFloatImmutablePair of(int left, float right) {
      return new IntFloatImmutablePair(left, right);
   }

   @Override
   public int leftInt() {
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
      } else if (other instanceof IntFloatPair) {
         return this.left == ((IntFloatPair)other).leftInt() && this.right == ((IntFloatPair)other).rightFloat();
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
      return "<" + this.leftInt() + "," + this.rightFloat() + ">";
   }
}
