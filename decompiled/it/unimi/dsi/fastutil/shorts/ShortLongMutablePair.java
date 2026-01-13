package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ShortLongMutablePair implements ShortLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected short left;
   protected long right;

   public ShortLongMutablePair(short left, long right) {
      this.left = left;
      this.right = right;
   }

   public static ShortLongMutablePair of(short left, long right) {
      return new ShortLongMutablePair(left, right);
   }

   @Override
   public short leftShort() {
      return this.left;
   }

   public ShortLongMutablePair left(short l) {
      this.left = l;
      return this;
   }

   @Override
   public long rightLong() {
      return this.right;
   }

   public ShortLongMutablePair right(long r) {
      this.right = r;
      return this;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortLongPair) {
         return this.left == ((ShortLongPair)other).leftShort() && this.right == ((ShortLongPair)other).rightLong();
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
      return "<" + this.leftShort() + "," + this.rightLong() + ">";
   }
}
