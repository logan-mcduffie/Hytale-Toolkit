package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleLongMutablePair implements DoubleLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected double left;
   protected long right;

   public DoubleLongMutablePair(double left, long right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleLongMutablePair of(double left, long right) {
      return new DoubleLongMutablePair(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   public DoubleLongMutablePair left(double l) {
      this.left = l;
      return this;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   public DoubleLongMutablePair right(long r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleLongPair) {
         return this.left == ((DoubleLongPair)other).leftDouble() && this.right == ((DoubleLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.double2int(this.left) * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftDouble() + "," + this.rightLong() + ">";
   }
}
