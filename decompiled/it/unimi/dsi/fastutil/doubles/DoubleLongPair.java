package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleLongPair extends Pair<Double, Long> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleLongPair left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleLongPair left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleLongPair first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleLongPair first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleLongPair key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleLongPair key(Double l) {
      return this.key(l.doubleValue());
   }

   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default DoubleLongPair right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleLongPair right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default DoubleLongPair second(long r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleLongPair second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default DoubleLongPair value(long r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleLongPair value(Long l) {
      return this.value(l.longValue());
   }

   static DoubleLongPair of(double left, long right) {
      return new DoubleLongImmutablePair(left, right);
   }

   static Comparator<DoubleLongPair> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : Long.compare(x.rightLong(), y.rightLong());
      };
   }
}
