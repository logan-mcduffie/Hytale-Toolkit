package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortIntPair extends Pair<Short, Integer> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortIntPair left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortIntPair left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortIntPair first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortIntPair first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortIntPair key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortIntPair key(Short l) {
      return this.key(l.shortValue());
   }

   int rightInt();

   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default ShortIntPair right(int r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortIntPair right(Integer l) {
      return this.right(l.intValue());
   }

   default int secondInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default ShortIntPair second(int r) {
      return this.right(r);
   }

   @Deprecated
   default ShortIntPair second(Integer l) {
      return this.second(l.intValue());
   }

   default int valueInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default ShortIntPair value(int r) {
      return this.right(r);
   }

   @Deprecated
   default ShortIntPair value(Integer l) {
      return this.value(l.intValue());
   }

   static ShortIntPair of(short left, int right) {
      return new ShortIntImmutablePair(left, right);
   }

   static Comparator<ShortIntPair> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
