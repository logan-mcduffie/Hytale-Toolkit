package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface IntLongPair extends Pair<Integer, Long> {
   int leftInt();

   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntLongPair left(int l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntLongPair left(Integer l) {
      return this.left(l.intValue());
   }

   default int firstInt() {
      return this.leftInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntLongPair first(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntLongPair first(Integer l) {
      return this.first(l.intValue());
   }

   default int keyInt() {
      return this.firstInt();
   }

   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntLongPair key(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntLongPair key(Integer l) {
      return this.key(l.intValue());
   }

   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default IntLongPair right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntLongPair right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default IntLongPair second(long r) {
      return this.right(r);
   }

   @Deprecated
   default IntLongPair second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default IntLongPair value(long r) {
      return this.right(r);
   }

   @Deprecated
   default IntLongPair value(Long l) {
      return this.value(l.longValue());
   }

   static IntLongPair of(int left, long right) {
      return new IntLongImmutablePair(left, right);
   }

   static Comparator<IntLongPair> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : Long.compare(x.rightLong(), y.rightLong());
      };
   }
}
