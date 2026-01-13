package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleIntPair extends Pair<Double, Integer> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleIntPair left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleIntPair left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleIntPair first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleIntPair first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleIntPair key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleIntPair key(Double l) {
      return this.key(l.doubleValue());
   }

   int rightInt();

   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default DoubleIntPair right(int r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleIntPair right(Integer l) {
      return this.right(l.intValue());
   }

   default int secondInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default DoubleIntPair second(int r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleIntPair second(Integer l) {
      return this.second(l.intValue());
   }

   default int valueInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default DoubleIntPair value(int r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleIntPair value(Integer l) {
      return this.value(l.intValue());
   }

   static DoubleIntPair of(double left, int right) {
      return new DoubleIntImmutablePair(left, right);
   }

   static Comparator<DoubleIntPair> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
