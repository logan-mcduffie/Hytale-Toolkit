package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ByteLongMutablePair implements ByteLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected byte left;
   protected long right;

   public ByteLongMutablePair(byte left, long right) {
      this.left = left;
      this.right = right;
   }

   public static ByteLongMutablePair of(byte left, long right) {
      return new ByteLongMutablePair(left, right);
   }

   @Override
   public byte leftByte() {
      return this.left;
   }

   public ByteLongMutablePair left(byte l) {
      this.left = l;
      return this;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   public ByteLongMutablePair right(long r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ByteLongPair) {
         return this.left == ((ByteLongPair)other).leftByte() && this.right == ((ByteLongPair)other).rightLong();
      } else {
         return !(other instanceof Pair) ? false : Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   @Override
   public int hashCode() {
      return this.left * 19 + HashCommon.long2int(this.right);
   }

   @Override
   public String toString() {
      return "<" + this.leftByte() + "," + this.rightLong() + ">";
   }
}
