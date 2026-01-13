package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteLongPair extends Pair<Byte, Long> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteLongPair left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteLongPair left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteLongPair first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteLongPair first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteLongPair key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteLongPair key(Byte l) {
      return this.key(l.byteValue());
   }

   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default ByteLongPair right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteLongPair right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default ByteLongPair second(long r) {
      return this.right(r);
   }

   @Deprecated
   default ByteLongPair second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default ByteLongPair value(long r) {
      return this.right(r);
   }

   @Deprecated
   default ByteLongPair value(Long l) {
      return this.value(l.longValue());
   }

   static ByteLongPair of(byte left, long right) {
      return new ByteLongImmutablePair(left, right);
   }

   static Comparator<ByteLongPair> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : Long.compare(x.rightLong(), y.rightLong());
      };
   }
}
