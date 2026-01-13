package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;
import java.util.Objects;

public class FloatFloatImmutableSortedPair extends FloatFloatImmutablePair implements FloatFloatSortedPair, Serializable {
   private static final long serialVersionUID = 0L;

   private FloatFloatImmutableSortedPair(float left, float right) {
      super(left, right);
   }

   public static FloatFloatImmutableSortedPair of(float left, float right) {
      return left <= right ? new FloatFloatImmutableSortedPair(left, right) : new FloatFloatImmutableSortedPair(right, left);
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof FloatFloatSortedPair) {
         return this.left == ((FloatFloatSortedPair)other).leftFloat() && this.right == ((FloatFloatSortedPair)other).rightFloat();
      } else {
         return !(other instanceof SortedPair)
            ? false
            : Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
      }
   }

   @Override
   public String toString() {
      return "{" + this.leftFloat() + "," + this.rightFloat() + "}";
   }
}
