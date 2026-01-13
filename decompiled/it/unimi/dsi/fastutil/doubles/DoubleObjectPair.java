package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleObjectPair<V> extends Pair<Double, V> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleObjectPair<V> left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleObjectPair<V> left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleObjectPair<V> first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleObjectPair<V> first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleObjectPair<V> key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleObjectPair<V> key(Double l) {
      return this.key(l.doubleValue());
   }

   static <V> DoubleObjectPair<V> of(double left, V right) {
      return new DoubleObjectImmutablePair<>(left, right);
   }

   static <V> Comparator<DoubleObjectPair<V>> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : ((Comparable)x.right()).compareTo(y.right());
      };
   }
}
