package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongLongPair extends Pair<Long, Long> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongLongPair left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongLongPair left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongLongPair first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongLongPair first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongLongPair key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongLongPair key(Long l) {
      return this.key(l.longValue());
   }

   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default LongLongPair right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongLongPair right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default LongLongPair second(long r) {
      return this.right(r);
   }

   @Deprecated
   default LongLongPair second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default LongLongPair value(long r) {
      return this.right(r);
   }

   @Deprecated
   default LongLongPair value(Long l) {
      return this.value(l.longValue());
   }

   static LongLongPair of(long left, long right) {
      return new LongLongImmutablePair(left, right);
   }

   static Comparator<LongLongPair> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : Long.compare(x.rightLong(), y.rightLong());
      };
   }
}
