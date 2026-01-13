package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntCharImmutablePair implements IntCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final int left;
   protected final char right;

   public IntCharImmutablePair(int left, char right) {
      this.left = left;
      this.right = right;
   }

   public static IntCharImmutablePair of(int left, char right) {
      return new IntCharImmutablePair(left, right);
   }

   @Override
   public int leftInt() {
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
      } else if (other instanceof IntCharPair) {
         return this.left == ((IntCharPair)other).leftInt() && this.right == ((IntCharPair)other).rightChar();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftInt() + "," + this.rightChar() + ">";
   }
}
