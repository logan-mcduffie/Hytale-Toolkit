package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortShortPair extends Pair<Short, Short> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortShortPair left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortShortPair left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortShortPair first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortShortPair first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortShortPair key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortShortPair key(Short l) {
      return this.key(l.shortValue());
   }

   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default ShortShortPair right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortShortPair right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default ShortShortPair second(short r) {
      return this.right(r);
   }

   @Deprecated
   default ShortShortPair second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default ShortShortPair value(short r) {
      return this.right(r);
   }

   @Deprecated
   default ShortShortPair value(Short l) {
      return this.value(l.shortValue());
   }

   static ShortShortPair of(short left, short right) {
      return new ShortShortImmutablePair(left, right);
   }

   static Comparator<ShortShortPair> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
