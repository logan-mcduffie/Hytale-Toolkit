package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatShortMutablePair implements FloatShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected float left;
   protected short right;

   public FloatShortMutablePair(float left, short right) {
      this.left = left;
      this.right = right;
   }

   public static FloatShortMutablePair of(float left, short right) {
      return new FloatShortMutablePair(left, right);
   }

   @Override
   public float leftFloat() {
      return this.left;
   }

   public FloatShortMutablePair left(float l) {
      this.left = l;
      return this;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   public FloatShortMutablePair right(short r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof FloatShortPair) {
         return this.left == ((FloatShortPair)other).leftFloat() && this.right == ((FloatShortPair)other).rightShort();
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
      return "<" + this.leftFloat() + "," + this.rightShort() + ">";
   }
}
