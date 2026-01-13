package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleShortPair extends Pair<Double, Short> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleShortPair left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleShortPair left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleShortPair first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleShortPair first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleShortPair key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleShortPair key(Double l) {
      return this.key(l.doubleValue());
   }

   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default DoubleShortPair right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleShortPair right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default DoubleShortPair second(short r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleShortPair second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default DoubleShortPair value(short r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleShortPair value(Short l) {
      return this.value(l.shortValue());
   }

   static DoubleShortPair of(double left, short right) {
      return new DoubleShortImmutablePair(left, right);
   }

   static Comparator<DoubleShortPair> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
