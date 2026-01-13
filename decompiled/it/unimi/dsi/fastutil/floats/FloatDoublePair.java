package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatDoublePair extends Pair<Float, Double> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatDoublePair left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatDoublePair left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatDoublePair first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatDoublePair first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatDoublePair key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatDoublePair key(Float l) {
      return this.key(l.floatValue());
   }

   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default FloatDoublePair right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatDoublePair right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default FloatDoublePair second(double r) {
      return this.right(r);
   }

   @Deprecated
   default FloatDoublePair second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default FloatDoublePair value(double r) {
      return this.right(r);
   }

   @Deprecated
   default FloatDoublePair value(Double l) {
      return this.value(l.doubleValue());
   }

   static FloatDoublePair of(float left, double right) {
      return new FloatDoubleImmutablePair(left, right);
   }

   static Comparator<FloatDoublePair> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
