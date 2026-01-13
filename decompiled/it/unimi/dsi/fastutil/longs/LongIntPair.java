package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongIntPair extends Pair<Long, Integer> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongIntPair left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongIntPair left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongIntPair first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongIntPair first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongIntPair key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongIntPair key(Long l) {
      return this.key(l.longValue());
   }

   int rightInt();

   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default LongIntPair right(int r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongIntPair right(Integer l) {
      return this.right(l.intValue());
   }

   default int secondInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default LongIntPair second(int r) {
      return this.right(r);
   }

   @Deprecated
   default LongIntPair second(Integer l) {
      return this.second(l.intValue());
   }

   default int valueInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default LongIntPair value(int r) {
      return this.right(r);
   }

   @Deprecated
   default LongIntPair value(Integer l) {
      return this.value(l.intValue());
   }

   static LongIntPair of(long left, int right) {
      return new LongIntImmutablePair(left, right);
   }

   static Comparator<LongIntPair> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
