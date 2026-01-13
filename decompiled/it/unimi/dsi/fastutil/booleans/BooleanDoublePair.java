package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanDoublePair extends Pair<Boolean, Double> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanDoublePair left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanDoublePair left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanDoublePair first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanDoublePair first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanDoublePair key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanDoublePair key(Boolean l) {
      return this.key(l.booleanValue());
   }

   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default BooleanDoublePair right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanDoublePair right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default BooleanDoublePair second(double r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanDoublePair second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default BooleanDoublePair value(double r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanDoublePair value(Double l) {
      return this.value(l.doubleValue());
   }

   static BooleanDoublePair of(boolean left, double right) {
      return new BooleanDoubleImmutablePair(left, right);
   }

   static Comparator<BooleanDoublePair> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
