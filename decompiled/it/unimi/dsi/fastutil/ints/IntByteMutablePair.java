package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntByteMutablePair implements IntBytePair, Serializable {
   private static final long serialVersionUID = 0L;
   protected int left;
   protected byte right;

   public IntByteMutablePair(int left, byte right) {
      this.left = left;
      this.right = right;
   }

   public static IntByteMutablePair of(int left, byte right) {
      return new IntByteMutablePair(left, right);
   }

   @Override
   public int leftInt() {
      return this.left;
   }

   public IntByteMutablePair left(int l) {
      this.left = l;
      return this;
   }

   @Override
   public byte rightByte() {
      return this.right;
   }

   public IntByteMutablePair right(byte r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof IntBytePair) {
         return this.left == ((IntBytePair)other).leftInt() && this.right == ((IntBytePair)other).rightByte();
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
      return "<" + this.leftInt() + "," + this.rightByte() + ">";
   }
}
