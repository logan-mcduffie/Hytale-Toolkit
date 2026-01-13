package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class FloatCharImmutablePair implements FloatCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final float left;
   protected final char right;

   public FloatCharImmutablePair(float left, char right) {
      this.left = left;
      this.right = right;
   }

   public static FloatCharImmutablePair of(float left, char right) {
      return new FloatCharImmutablePair(left, right);
   }

   @Override
   public float leftFloat() {
      return this.left;
   }

   @Override
   public char rightChar() {
      return this.right;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof FloatCharPair) {
         return this.left == ((FloatCharPair)other).leftFloat() && this.right == ((FloatCharPair)other).rightChar();
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
      return "<" + this.leftFloat() + "," + this.rightChar() + ">";
   }
}
