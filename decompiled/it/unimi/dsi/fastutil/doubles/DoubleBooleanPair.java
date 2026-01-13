package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleBooleanPair extends Pair<Double, Boolean> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleBooleanPair left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleBooleanPair left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleBooleanPair first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleBooleanPair first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleBooleanPair key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleBooleanPair key(Double l) {
      return this.key(l.doubleValue());
   }

   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default DoubleBooleanPair right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleBooleanPair right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default DoubleBooleanPair second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleBooleanPair second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default DoubleBooleanPair value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleBooleanPair value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static DoubleBooleanPair of(double left, boolean right) {
      return new DoubleBooleanImmutablePair(left, right);
   }

   static Comparator<DoubleBooleanPair> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
