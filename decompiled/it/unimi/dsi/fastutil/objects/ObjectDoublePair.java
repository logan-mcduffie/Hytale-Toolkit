package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ObjectDoublePair<K> extends Pair<K, Double> {
   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default ObjectDoublePair<K> right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ObjectDoublePair<K> right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default ObjectDoublePair<K> second(double r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectDoublePair<K> second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default ObjectDoublePair<K> value(double r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectDoublePair<K> value(Double l) {
      return this.value(l.doubleValue());
   }

   static <K> ObjectDoublePair<K> of(K left, double right) {
      return new ObjectDoubleImmutablePair<>(left, right);
   }

   static <K> Comparator<ObjectDoublePair<K>> lexComparator() {
      return (x, y) -> {
         int t = ((Comparable)x.left()).compareTo(y.left());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
