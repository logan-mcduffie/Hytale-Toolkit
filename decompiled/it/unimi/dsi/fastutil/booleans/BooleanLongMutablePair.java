package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanLongMutablePair implements BooleanLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected boolean left;
   protected long right;

   public BooleanLongMutablePair(boolean left, long right) {
      this.left = left;
      this.right = right;
   }

   public static BooleanLongMutablePair of(boolean left, long right) {
      return new BooleanLongMutablePair(left, right);
   }

   @Override
   public boolean leftBoolean() {
      return this.left;
   }

   public BooleanLongMutablePair left(boolean l) {
      this.left = l;
      return this;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   public BooleanLongMutablePair right(long r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof BooleanLongPair) {
         return this.left == ((BooleanLongPair)other).leftBoolean() && this.right == ((BooleanLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return (this.left ? 1231 : 1237) * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftBoolean() + "," + this.rightLong() + ">";
   }
}
