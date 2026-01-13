package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleCharMutablePair implements DoubleCharPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected double left;
   protected char right;

   public DoubleCharMutablePair(double left, char right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleCharMutablePair of(double left, char right) {
      return new DoubleCharMutablePair(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   public DoubleCharMutablePair left(double l) {
      this.left = l;
      return this;
   }

   @Override
   public char rightChar() {
      return this.right;
   }

   public DoubleCharMutablePair right(char r) {
      this.right = r;
      return this;
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
