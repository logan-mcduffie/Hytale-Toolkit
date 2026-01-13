package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleCharImmutablePair implements DoubleCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final double left;
   protected final char right;

   public DoubleCharImmutablePair(double left, char right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleCharImmutablePair of(double left, char right) {
      return new DoubleCharImmutablePair(left, right);
   }

   @Override
   public double leftDouble() {
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
      } else if (other instanceof DoubleCharPair) {
         return this.left == ((DoubleCharPair)other).leftDouble() && this.right == ((DoubleCharPair)other).rightChar();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightChar() + ">";
   }
}
