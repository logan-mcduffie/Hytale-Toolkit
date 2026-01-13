package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;
import java.util.Objects;

public class DoubleDoubleImmutableSortedPair extends DoubleDoubleImmutablePair implements DoubleDoubleSortedPair, Serializable {
   private static final long serialVersionUID = 0L;

   private DoubleDoubleImmutableSortedPair(double left, double right) {
      super(left, right);
   }

   public static DoubleDoubleImmutableSortedPair of(double left, double right) {
      return left <= right ? new DoubleDoubleImmutableSortedPair(left, right) : new DoubleDoubleImmutableSortedPair(right, left);
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof DoubleDoubleSortedPair) {
         return this.left == ((DoubleDoubleSortedPair)other).leftDouble() && this.right == ((DoubleDoubleSortedPair)other).rightDouble();
      } else {
         return !(other instanceof SortedPair)
            ? false
            : Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
      }
   }

   @Override
   public String toString() {
      return "{" + this.leftDouble() + "," + this.rightDouble() + "}";
   }
}
