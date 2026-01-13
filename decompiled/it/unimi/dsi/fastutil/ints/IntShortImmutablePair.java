package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntShortImmutablePair implements IntShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final int left;
   protected final short right;

   public IntShortImmutablePair(int left, short right) {
      this.left = left;
      this.right = right;
   }

   public static IntShortImmutablePair of(int left, short right) {
      return new IntShortImmutablePair(left, right);
   }

   @Override
   public int leftInt() {
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
      } else if (other instanceof IntShortPair) {
         return this.left == ((IntShortPair)other).leftInt() && this.right == ((IntShortPair)other).rightShort();
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
      return "<" + this.leftInt() + "," + this.rightShort() + ">";
   }
}
