package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface BooleanComparator extends Comparator<Boolean> {
   int compare(boolean var1, boolean var2);

   default BooleanComparator reversed() {
      return BooleanComparators.oppositeComparator(this);
   }

   @Deprecated
   default int compare(Boolean ok1, Boolean ok2) {
      return this.compare(ok1.booleanValue(), ok2.booleanValue());
   }

   default BooleanComparator thenComparing(BooleanComparator second) {
      return (BooleanComparator)((Serializable)((k1, k2) -> {
         int comp = this.compare(k1, k2);
         return comp == 0 ? second.compare(k1, k2) : comp;
      }));
   }

   @Override
   default Comparator<Boolean> thenComparing(Comparator<? super Boolean> second) {
      return (Comparator<Boolean>)(second instanceof BooleanComparator ? this.thenComparing((BooleanComparator)second) : Comparator.super.thenComparing(second));
   }
}
