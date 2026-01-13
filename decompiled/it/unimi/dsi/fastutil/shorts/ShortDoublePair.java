package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortDoublePair extends Pair<Short, Double> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortDoublePair left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortDoublePair left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortDoublePair first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortDoublePair first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortDoublePair key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortDoublePair key(Short l) {
      return this.key(l.shortValue());
   }

   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default ShortDoublePair right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortDoublePair right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default ShortDoublePair second(double r) {
      return this.right(r);
   }

   @Deprecated
   default ShortDoublePair second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default ShortDoublePair value(double r) {
      return this.right(r);
   }

   @Deprecated
   default ShortDoublePair value(Double l) {
      return this.value(l.doubleValue());
   }

   static ShortDoublePair of(short left, double right) {
      return new ShortDoubleImmutablePair(left, right);
   }

   static Comparator<ShortDoublePair> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
