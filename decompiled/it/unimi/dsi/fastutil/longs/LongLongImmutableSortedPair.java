package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;
import java.util.Objects;

public class LongLongImmutableSortedPair extends LongLongImmutablePair implements LongLongSortedPair, Serializable {
   private static final long serialVersionUID = 0L;

   private LongLongImmutableSortedPair(long left, long right) {
      super(left, right);
   }

   public static LongLongImmutableSortedPair of(long left, long right) {
      return left <= right ? new LongLongImmutableSortedPair(left, right) : new LongLongImmutableSortedPair(right, left);
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof LongLongSortedPair) {
         return this.left == ((LongLongSortedPair)other).leftLong() && this.right == ((LongLongSortedPair)other).rightLong();
      } else {
         return !(other instanceof SortedPair)
            ? false
            : Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
      }
   }

   @Override
   public String toString() {
      return "{" + this.leftLong() + "," + this.rightLong() + "}";
   }
}
