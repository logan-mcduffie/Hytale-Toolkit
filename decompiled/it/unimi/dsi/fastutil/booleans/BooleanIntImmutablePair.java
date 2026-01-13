package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanIntImmutablePair implements BooleanIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final int right;

   public BooleanIntImmutablePair(boolean left, int right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanIntImmutablePair of(boolean left, int right) {
      return new BooleanIntImmutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
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
      } else if (other instanceof BooleanIntPair) {
         return this.left == ((BooleanIntPair)other).leftBoolean() && this.right == ((BooleanIntPair)other).rightInt();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.rightInt() + ">";
   }
}
