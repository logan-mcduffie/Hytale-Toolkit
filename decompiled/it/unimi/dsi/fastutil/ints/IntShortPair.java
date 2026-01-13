package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface IntShortPair extends Pair<Integer, Short> {
   int leftInt();

   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntShortPair left(int l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntShortPair left(Integer l) {
      return this.left(l.intValue());
   }

   default int firstInt() {
      return this.leftInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntShortPair first(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntShortPair first(Integer l) {
      return this.first(l.intValue());
   }

   default int keyInt() {
      return this.firstInt();
   }

   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntShortPair key(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntShortPair key(Integer l) {
      return this.key(l.intValue());
   }

   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default IntShortPair right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntShortPair right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default IntShortPair second(short r) {
      return this.right(r);
   }

   @Deprecated
   default IntShortPair second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default IntShortPair value(short r) {
      return this.right(r);
   }

   @Deprecated
   default IntShortPair value(Short l) {
      return this.value(l.shortValue());
   }

   static IntShortPair of(int left, short right) {
      return new IntShortImmutablePair(left, right);
   }

   static Comparator<IntShortPair> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
