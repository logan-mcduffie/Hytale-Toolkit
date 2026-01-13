package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongShortPair extends Pair<Long, Short> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongShortPair left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongShortPair left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongShortPair first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongShortPair first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongShortPair key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongShortPair key(Long l) {
      return this.key(l.longValue());
   }

   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default LongShortPair right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongShortPair right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default LongShortPair second(short r) {
      return this.right(r);
   }

   @Deprecated
   default LongShortPair second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default LongShortPair value(short r) {
      return this.right(r);
   }

   @Deprecated
   default LongShortPair value(Short l) {
      return this.value(l.shortValue());
   }

   static LongShortPair of(long left, short right) {
      return new LongShortImmutablePair(left, right);
   }

   static Comparator<LongShortPair> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
