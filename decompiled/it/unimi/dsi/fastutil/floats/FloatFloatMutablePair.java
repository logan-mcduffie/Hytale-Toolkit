package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatFloatMutablePair implements FloatFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected float left;
   protected float right;

   public FloatFloatMutablePair(float left, float right) {
      this.left = left;
      this.right = right;
   }

   public static FloatFloatMutablePair of(float left, float right) {
      return new FloatFloatMutablePair(left, right);
   }

   @Override
   public float leftFloat() {
      return this.left;
   }

   public FloatFloatMutablePair left(float l) {
      this.left = l;
      return this;
   }

   @Override
   public float rightFloat() {
      return this.right;
   }

   public FloatFloatMutablePair right(float r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof FloatFloatPair) {
         return this.left == ((FloatFloatPair)other).leftFloat() && this.right == ((FloatFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.float2int(this.left) * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftFloat() + "," + this.rightFloat() + ">";
   }
}
