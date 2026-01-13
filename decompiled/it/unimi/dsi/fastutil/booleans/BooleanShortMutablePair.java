package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanShortMutablePair implements BooleanShortPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected boolean left;
   protected short right;

   public BooleanShortMutablePair(boolean left, short right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanShortMutablePair of(boolean left, short right) {
      return new BooleanShortMutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
      return this.left;
   }

   public BooleanShortMutablePair left(boolean l) {
      this.left = l;
      return this;
   }

   @Override
   public short rightShort() {
      return this.right;
   }

   public BooleanShortMutablePair right(short r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof BooleanShortPair) {
         return this.left == ((BooleanShortPair)other).leftBoolean() && this.right == ((BooleanShortPair)other).rightShort();
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
      return "<" + this.leftBoolean() + "," + this.rightShort() + ">";
   }
}
