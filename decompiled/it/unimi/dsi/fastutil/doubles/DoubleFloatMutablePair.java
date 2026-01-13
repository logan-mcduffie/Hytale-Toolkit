package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleFloatMutablePair implements DoubleFloatPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected double left;
   protected float right;

   public DoubleFloatMutablePair(double left, float right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleFloatMutablePair of(double left, float right) {
      return new DoubleFloatMutablePair(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   public DoubleFloatMutablePair left(double l) {
      this.left = l;
      return this;
   }

   @Override
   public float rightFloat() {
      return this.right;
   }

   public DoubleFloatMutablePair right(float r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleFloatPair) {
         return this.left == ((DoubleFloatPair)other).leftDouble() && this.right == ((DoubleFloatPair)other).rightFloat();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + HashCommon.float2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightFloat() + ">";
   }
}
