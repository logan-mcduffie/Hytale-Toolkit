package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleDoubleMutablePair implements DoubleDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected double left;
   protected double right;

   public DoubleDoubleMutablePair(double left, double right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleDoubleMutablePair of(double left, double right) {
      return new DoubleDoubleMutablePair(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   public DoubleDoubleMutablePair left(double l) {
      this.left = l;
      return this;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   public DoubleDoubleMutablePair right(double r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleDoublePair) {
         return this.left == ((DoubleDoublePair)other).leftDouble() && this.right == ((DoubleDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightDouble() + ">";
   }
}
