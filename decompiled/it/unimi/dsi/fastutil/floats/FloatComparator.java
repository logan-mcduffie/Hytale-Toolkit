package it.unimi.dsi.fastutil.floats;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface FloatComparator extends Comparator<Float> {
   int compare(float var1, float var2);

   default FloatComparator reversed() {
      return FloatComparators.oppositeComparator(this);
   }

   @Deprecated
   default int compare(Float ok1, Float ok2) {
      return this.compare(ok1.floatValue(), ok2.floatValue());
   }

   default FloatComparator thenComparing(FloatComparator second) {
      return (FloatComparator)((Serializable)((k1, k2) -> {
         int comp = this.compare(k1, k2);
         return comp == 0 ? second.compare(k1, k2) : comp;
      }));
   }

   @Override
   default Comparator<Float> thenComparing(Comparator<? super Float> second) {
      return (Comparator<Float>)(second instanceof FloatComparator ? this.thenComparing((FloatComparator)second) : Comparator.super.thenComparing(second));
   }
}
