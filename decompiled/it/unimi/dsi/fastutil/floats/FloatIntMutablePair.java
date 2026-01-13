package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatIntMutablePair implements FloatIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected float left;
   protected int right;

   public FloatIntMutablePair(float left, int right) {
      this.left = left;
      this.right = right;
   }

   public static FloatIntMutablePair of(float left, int right) {
      return new FloatIntMutablePair(left, right);
   }

   @Override
   public float leftFloat() {
      return this.left;
   }

   public FloatIntMutablePair left(float l) {
      this.left = l;
      return this;
   }

   @Override
   public int rightInt() {
      return this.right;
   }

   public FloatIntMutablePair right(int r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof FloatIntPair) {
         return this.left == ((FloatIntPair)other).leftFloat() && this.right == ((FloatIntPair)other).rightInt();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.float2int(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftFloat() + "," + this.rightInt() + ">";
   }
}
