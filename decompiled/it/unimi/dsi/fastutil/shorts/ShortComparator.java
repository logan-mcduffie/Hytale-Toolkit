package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface ShortComparator extends Comparator<Short> {
   int compare(short var1, short var2);

   default ShortComparator reversed() {
      return ShortComparators.oppositeComparator(this);
   }

   @Deprecated
   default int compare(Short ok1, Short ok2) {
      return this.compare(ok1.shortValue(), ok2.shortValue());
   }

   default ShortComparator thenComparing(ShortComparator second) {
      return (ShortComparator)((Serializable)((k1, k2) -> {
         int comp = this.compare(k1, k2);
         return comp == 0 ? second.compare(k1, k2) : comp;
      }));
   }

   @Override
   default Comparator<Short> thenComparing(Comparator<? super Short> second) {
      return (Comparator<Short>)(second instanceof ShortComparator ? this.thenComparing((ShortComparator)second) : Comparator.super.thenComparing(second));
   }
}
