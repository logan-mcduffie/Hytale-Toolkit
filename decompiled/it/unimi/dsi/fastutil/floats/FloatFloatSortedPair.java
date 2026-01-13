package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;

public interface FloatFloatSortedPair extends FloatFloatPair, SortedPair<Float>, Serializable {
   static FloatFloatSortedPair of(float left, float right) {
      return FloatFloatImmutableSortedPair.of(left, right);
   }

   default boolean contains(float e) {
      return e == this.leftFloat() || e == this.rightFloat();
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return o == null ? false : this.contains(((Float)o).floatValue());
   }
}
