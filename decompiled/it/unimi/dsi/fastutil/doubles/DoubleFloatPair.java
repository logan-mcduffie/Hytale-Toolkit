package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleFloatPair extends Pair<Double, Float> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleFloatPair left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleFloatPair left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleFloatPair first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleFloatPair first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleFloatPair key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleFloatPair key(Double l) {
      return this.key(l.doubleValue());
   }

   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default DoubleFloatPair right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleFloatPair right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default DoubleFloatPair second(float r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleFloatPair second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default DoubleFloatPair value(float r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleFloatPair value(Float l) {
      return this.value(l.floatValue());
   }

   static DoubleFloatPair of(double left, float right) {
      return new DoubleFloatImmutablePair(left, right);
   }

   static Comparator<DoubleFloatPair> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
