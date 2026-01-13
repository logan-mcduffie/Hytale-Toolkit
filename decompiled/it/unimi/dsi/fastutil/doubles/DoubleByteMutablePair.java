package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleByteMutablePair implements DoubleBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected double left;
   protected byte right;

   public DoubleByteMutablePair(double left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static DoubleByteMutablePair of(double left, byte right) {
      return new DoubleByteMutablePair(left, right);
   }

   @Override
   public double leftDouble() {
      return this.left;
   }

   public DoubleByteMutablePair left(double l) {
      this.left = l;
      return this;
   }

   @Override
   public byte rightByte() {
      return this.right;
   }

   public DoubleByteMutablePair right(byte r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleBytePair) {
         return this.left == ((DoubleBytePair)other).leftDouble() && this.right == ((DoubleBytePair)other).rightByte();
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
      return "<" + this.leftDouble() + "," + this.rightByte() + ">";
   }
}
