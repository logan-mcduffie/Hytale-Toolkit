package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleBooleanMutablePair implements DoubleBooleanPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected double left;
   protected boolean right;

   public DoubleBooleanMutablePair(double left, boolean right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleBooleanMutablePair of(double left, boolean right) {
      return new DoubleBooleanMutablePair(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   public DoubleBooleanMutablePair left(double l) {
      this.left = l;
      return this;
   }

   @Override
   public boolean rightBoolean() {
      return this.right;
   }

   public DoubleBooleanMutablePair right(boolean r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleBooleanPair) {
         return this.left == ((DoubleBooleanPair)other).leftDouble() && this.right == ((DoubleBooleanPair)other).rightBoolean();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + (this.right ? 1231 : 1237);
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightBoolean() + ">";
   }
}
