package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanFloatMutablePair implements BooleanFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected boolean left;
   protected float right;

   public BooleanFloatMutablePair(boolean left, float right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanFloatMutablePair of(boolean left, float right) {
      return new BooleanFloatMutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
      return this.left;
   }

   public BooleanFloatMutablePair left(boolean l) {
      this.left = l;
      return this;
   }

   @Override
   public float rightFloat() {
      return this.right;
   }

   public BooleanFloatMutablePair right(float r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof BooleanFloatPair) {
         return this.left == ((BooleanFloatPair)other).leftBoolean() && this.right == ((BooleanFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.rightFloat() + ">";
   }
}
