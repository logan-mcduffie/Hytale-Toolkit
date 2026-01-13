package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanCharImmutablePair implements BooleanCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final boolean left;
   protected final char right;

   public BooleanCharImmutablePair(boolean left, char right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanCharImmutablePair of(boolean left, char right) {
      return new BooleanCharImmutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
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
      } else if (other instanceof BooleanCharPair) {
         return this.left == ((BooleanCharPair)other).leftBoolean() && this.right == ((BooleanCharPair)other).rightChar();
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
      return "<" + this.leftBoolean() + "," + this.rightChar() + ">";
   }
}
