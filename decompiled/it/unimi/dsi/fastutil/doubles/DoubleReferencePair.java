package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;

public interface DoubleReferencePair<V> extends Pair<Double, V> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleReferencePair<V> left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleReferencePair<V> left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleReferencePair<V> first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleReferencePair<V> first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleReferencePair<V> key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleReferencePair<V> key(Double l) {
      return this.key(l.doubleValue());
   }

   static <V> DoubleReferencePair<V> of(double left, V right) {
      return new DoubleReferenceImmutablePair<>(left, right);
   }
}
