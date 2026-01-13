package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongFloatPair extends Pair<Long, Float> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongFloatPair left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongFloatPair left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongFloatPair first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongFloatPair first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongFloatPair key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongFloatPair key(Long l) {
      return this.key(l.longValue());
   }

   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default LongFloatPair right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongFloatPair right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default LongFloatPair second(float r) {
      return this.right(r);
   }

   @Deprecated
   default LongFloatPair second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default LongFloatPair value(float r) {
      return this.right(r);
   }

   @Deprecated
   default LongFloatPair value(Float l) {
      return this.value(l.floatValue());
   }

   static LongFloatPair of(long left, float right) {
      return new LongFloatImmutablePair(left, right);
   }

   static Comparator<LongFloatPair> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
