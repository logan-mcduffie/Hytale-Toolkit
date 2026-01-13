package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface LongComparator extends Comparator<Long> {
   int compare(long var1, long var3);

   default LongComparator reversed() {
      return LongComparators.oppositeComparator(this);
   }

   @Deprecated
   default int compare(Long ok1, Long ok2) {
      return this.compare(ok1.longValue(), ok2.longValue());
   }

   default LongComparator thenComparing(LongComparator second) {
      return (LongComparator)((Serializable)((k1, k2) -> {
         int comp = this.compare(k1, k2);
         return comp == 0 ? second.compare(k1, k2) : comp;
      }));
   }

   @Override
   default Comparator<Long> thenComparing(Comparator<? super Long> second) {
      return (Comparator<Long>)(second instanceof LongComparator ? this.thenComparing((LongComparator)second) : Comparator.super.thenComparing(second));
   }
}
