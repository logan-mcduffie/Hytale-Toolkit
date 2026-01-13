package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface DoubleComparator extends Comparator<Double> {
   int compare(double var1, double var3);

   default DoubleComparator reversed() {
      return DoubleComparators.oppositeComparator(this);
   }

   @Deprecated
   default int compare(Double ok1, Double ok2) {
      return this.compare(ok1.doubleValue(), ok2.doubleValue());
   }

   default DoubleComparator thenComparing(DoubleComparator second) {
      return (DoubleComparator)((Serializable)((k1, k2) -> {
         int comp = this.compare(k1, k2);
         return comp == 0 ? second.compare(k1, k2) : comp;
      }));
   }

   @Override
   default Comparator<Double> thenComparing(Comparator<? super Double> second) {
      return (Comparator<Double>)(second instanceof DoubleComparator ? this.thenComparing((DoubleComparator)second) : Comparator.super.thenComparing(second));
   }
}
