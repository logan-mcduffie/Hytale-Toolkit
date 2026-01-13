package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;

public interface DoubleDoubleSortedPair extends DoubleDoublePair, SortedPair<Double>, Serializable {
   static DoubleDoubleSortedPair of(double left, double right) {
      return DoubleDoubleImmutableSortedPair.of(left, right);
   }

   default boolean contains(double e) {
      return e == this.leftDouble() || e == this.rightDouble();
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return o == null ? false : this.contains(((Double)o).doubleValue());
   }
}
