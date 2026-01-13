package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleIntImmutablePair implements DoubleIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final double left;
   protected final int right;

   public DoubleIntImmutablePair(double left, int right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleIntImmutablePair of(double left, int right) {
      return new DoubleIntImmutablePair(left, right);
   }

   @Override
   public double leftDouble() {
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
      } else if (other instanceof DoubleIntPair) {
         return this.left == ((DoubleIntPair)other).leftDouble() && this.right == ((DoubleIntPair)other).rightInt();
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
      return "<" + this.leftDouble() + "," + this.rightInt() + ">";
   }
}
