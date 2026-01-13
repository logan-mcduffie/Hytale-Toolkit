package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatShortImmutablePair implements FloatShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final float left;
   protected final short right;

   public FloatShortImmutablePair(float left, short right) {
      this.left = left;
      this.right = right;
   }

   public static FloatShortImmutablePair of(float left, short right) {
      return new FloatShortImmutablePair(left, right);
   }

   @Override
   public float leftFloat() {
      return this.left;
   }

   @Override
   public short rightShort() {
      return this.right;
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
