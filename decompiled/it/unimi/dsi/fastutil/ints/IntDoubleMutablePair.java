package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntDoubleMutablePair implements IntDoublePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected int left;
   protected double right;

   public IntDoubleMutablePair(int left, double right) {
      this.left = left;
      this.right = right;
   }

   public static IntDoubleMutablePair of(int left, double right) {
      return new IntDoubleMutablePair(left, right);
   }

   @Override
   public int leftInt() {
      return this.left;
   }

   public IntDoubleMutablePair left(int l) {
      this.left = l;
      return this;
   }

   @Override
   public double rightDouble() {
      return this.right;
   }

   public IntDoubleMutablePair right(double r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof IntDoublePair) {
         return this.left == ((IntDoublePair)other).leftInt() && this.right == ((IntDoublePair)other).rightDouble();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.double2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftInt() + "," + this.rightDouble() + ">";
   }
}
