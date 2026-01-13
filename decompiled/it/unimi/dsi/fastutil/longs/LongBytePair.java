package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongBytePair extends Pair<Long, Byte> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongBytePair left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongBytePair left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongBytePair first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongBytePair first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongBytePair key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongBytePair key(Long l) {
      return this.key(l.longValue());
   }

   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default LongBytePair right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongBytePair right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default LongBytePair second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default LongBytePair second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default LongBytePair value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default LongBytePair value(Byte l) {
      return this.value(l.byteValue());
   }

   static LongBytePair of(long left, byte right) {
      return new LongByteImmutablePair(left, right);
   }

   static Comparator<LongBytePair> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
