package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongIntMutablePair implements LongIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected long left;
   protected int right;

   public LongIntMutablePair(long left, int right) {
      this.left = left;
      this.right = right;
   }

   public static LongIntMutablePair of(long left, int right) {
      return new LongIntMutablePair(left, right);
   }

   @Override
   public long leftLong() {
      return this.left;
   }

   public LongIntMutablePair left(long l) {
      this.left = l;
      return this;
   }

   @Override
   public int rightInt() {
      return this.right;
   }

   public LongIntMutablePair right(int r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof LongIntPair) {
         return this.left == ((LongIntPair)other).leftLong() && this.right == ((LongIntPair)other).rightInt();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.long2int(this.left) * 19 + this.right;
   }

   @Override
   public String toString() {
      return "<" + this.leftLong() + "," + this.rightInt() + ">";
   }
}
