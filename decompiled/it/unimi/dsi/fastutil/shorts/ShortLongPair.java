package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortLongPair extends Pair<Short, Long> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortLongPair left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortLongPair left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortLongPair first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortLongPair first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortLongPair key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortLongPair key(Short l) {
      return this.key(l.shortValue());
   }

   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default ShortLongPair right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortLongPair right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default ShortLongPair second(long r) {
      return this.right(r);
   }

   @Deprecated
   default ShortLongPair second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default ShortLongPair value(long r) {
      return this.right(r);
   }

   @Deprecated
   default ShortLongPair value(Long l) {
      return this.value(l.longValue());
   }

   static ShortLongPair of(short left, long right) {
      return new ShortLongImmutablePair(left, right);
   }

   static Comparator<ShortLongPair> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : Long.compare(x.rightLong(), y.rightLong());
      };
   }
}
