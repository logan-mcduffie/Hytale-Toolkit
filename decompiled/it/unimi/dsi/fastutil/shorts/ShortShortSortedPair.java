package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;

public interface ShortShortSortedPair extends ShortShortPair, SortedPair<Short>, Serializable {
   static ShortShortSortedPair of(short left, short right) {
      return ShortShortImmutableSortedPair.of(left, right);
   }

   default boolean contains(short e) {
      return e == this.leftShort() || e == this.rightShort();
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return o == null ? false : this.contains(((Short)o).shortValue());
   }
}
