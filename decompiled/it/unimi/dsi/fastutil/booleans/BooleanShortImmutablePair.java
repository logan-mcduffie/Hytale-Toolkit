package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanShortImmutablePair implements BooleanShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final short right;

   public BooleanShortImmutablePair(boolean left, short right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanShortImmutablePair of(boolean left, short right) {
      return new BooleanShortImmutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
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
      } else if (other instanceof BooleanShortPair) {
         return this.left == ((BooleanShortPair)other).leftBoolean() && this.right == ((BooleanShortPair)other).rightShort();
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
      return "<" + this.leftBoolean() + "," + this.rightShort() + ">";
   }
}
