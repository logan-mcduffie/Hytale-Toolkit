package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceDoublePair<K> extends Pair<K, Double> {
   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default ReferenceDoublePair<K> right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ReferenceDoublePair<K> right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default ReferenceDoublePair<K> second(double r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceDoublePair<K> second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default ReferenceDoublePair<K> value(double r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceDoublePair<K> value(Double l) {
      return this.value(l.doubleValue());
   }

   static <K> ReferenceDoublePair<K> of(K left, double right) {
      return new ReferenceDoubleImmutablePair<>(left, right);
   }
}
