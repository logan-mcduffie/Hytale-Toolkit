package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongDoublePair extends Pair<Long, Double> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongDoublePair left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongDoublePair left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongDoublePair first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongDoublePair first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongDoublePair key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongDoublePair key(Long l) {
      return this.key(l.longValue());
   }

   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default LongDoublePair right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongDoublePair right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default LongDoublePair second(double r) {
      return this.right(r);
   }

   @Deprecated
   default LongDoublePair second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default LongDoublePair value(double r) {
      return this.right(r);
   }

   @Deprecated
   default LongDoublePair value(Double l) {
      return this.value(l.doubleValue());
   }

   static LongDoublePair of(long left, double right) {
      return new LongDoubleImmutablePair(left, right);
   }

   static Comparator<LongDoublePair> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
