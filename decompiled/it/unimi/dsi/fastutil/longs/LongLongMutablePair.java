package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongLongMutablePair implements LongLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected long left;
   protected long right;

   public LongLongMutablePair(long left, long right) {
      this.left = left;
      this.right = right;
   }

   public static LongLongMutablePair of(long left, long right) {
      return new LongLongMutablePair(left, right);
   }

   @Override
   public long leftLong() {
      return this.left;
   }

   public LongLongMutablePair left(long l) {
      this.left = l;
      return this;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   public LongLongMutablePair right(long r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof LongLongPair) {
         return this.left == ((LongLongPair)other).leftLong() && this.right == ((LongLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return HashCommon.long2int(this.left) * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftLong() + "," + this.rightLong() + ">";
   }
}
