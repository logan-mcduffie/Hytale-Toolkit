package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanIntMutablePair implements BooleanIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected boolean left;
   protected int right;

   public BooleanIntMutablePair(boolean left, int right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanIntMutablePair of(boolean left, int right) {
      return new BooleanIntMutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
      return this.left;
   }

   public BooleanIntMutablePair left(boolean l) {
      this.left = l;
      return this;
   }

   @Override
   public int rightInt() {
      return this.right;
   }

   public BooleanIntMutablePair right(int r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof BooleanIntPair) {
         return this.left == ((BooleanIntPair)other).leftBoolean() && this.right == ((BooleanIntPair)other).rightInt();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.rightInt() + ">";
   }
}
