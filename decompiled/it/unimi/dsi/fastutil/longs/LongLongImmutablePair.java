package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class LongLongImmutablePair implements LongLongPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected final long left;
   protected final long right;

   public LongLongImmutablePair(long left, long right) {
      this.left = left;
      this.right = right;
   }

   public static LongLongImmutablePair of(long left, long right) {
      return new LongLongImmutablePair(left, right);
   }

   @Override
   public long leftLong() {
      return this.left;
   }

   @Override
   public long rightLong() {
      return this.right;
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
