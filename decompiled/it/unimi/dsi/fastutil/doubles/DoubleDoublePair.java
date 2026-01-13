package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleDoublePair extends Pair<Double, Double> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleDoublePair left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleDoublePair left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleDoublePair first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleDoublePair first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleDoublePair key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleDoublePair key(Double l) {
      return this.key(l.doubleValue());
   }

   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default DoubleDoublePair right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleDoublePair right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default DoubleDoublePair second(double r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleDoublePair second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default DoubleDoublePair value(double r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleDoublePair value(Double l) {
      return this.value(l.doubleValue());
   }

   static DoubleDoublePair of(double left, double right) {
      return new DoubleDoubleImmutablePair(left, right);
   }

   static Comparator<DoubleDoublePair> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
