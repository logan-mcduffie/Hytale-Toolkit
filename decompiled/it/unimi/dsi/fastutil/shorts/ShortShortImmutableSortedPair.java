package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortShortImmutableSortedPair extends ShortShortImmutablePair implements ShortShortSortedPair, Serializable {
   private static final long serialVersionUID = 0L;

   private ShortShortImmutableSortedPair(short left, short right) {
      super(left, right);
   }

   public static ShortShortImmutableSortedPair of(short left, short right) {
      return left <= right ? new ShortShortImmutableSortedPair(left, right) : new ShortShortImmutableSortedPair(right, left);
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ShortShortSortedPair) {
         return this.left == ((ShortShortSortedPair)other).leftShort() && this.right == ((ShortShortSortedPair)other).rightShort();
      } else {
         return !(other instanceof SortedPair)
            ? false
            : Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
      }
   }

   @Override
   public String toString() {
      return "{" + this.leftShort() + "," + this.rightShort() + "}";
   }
}
