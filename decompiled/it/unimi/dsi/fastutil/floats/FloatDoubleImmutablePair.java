package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatDoubleImmutablePair implements FloatDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final float left;
   protected final double right;

   public FloatDoubleImmutablePair(float left, double right) {
      this.left = left;
      this.right = right;
   }

   public static FloatDoubleImmutablePair of(float left, double right) {
      return new FloatDoubleImmutablePair(left, right);
   }

   @Override
   public float leftFloat() {
      return this.left;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof FloatDoublePair) {
         return this.left == ((FloatDoublePair)other).leftFloat() && this.right == ((FloatDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.float2int(this.left) * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftFloat() + "," + this.rightDouble() + ">";
   }
}
