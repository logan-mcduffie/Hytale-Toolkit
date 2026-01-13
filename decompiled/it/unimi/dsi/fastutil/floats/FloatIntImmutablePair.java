package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatIntImmutablePair implements FloatIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final float left;
   protected final int right;

   public FloatIntImmutablePair(float left, int right) {
      this.left = left;
      this.right = right;
   }

   public static FloatIntImmutablePair of(float left, int right) {
      return new FloatIntImmutablePair(left, right);
   }

   @Override
   public float leftFloat() {
      return this.left;
   }

   @Override
   public int rightInt() {
      return this.right;
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
