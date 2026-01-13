package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteShortPair extends Pair<Byte, Short> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteShortPair left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteShortPair left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteShortPair first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteShortPair first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteShortPair key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteShortPair key(Byte l) {
      return this.key(l.byteValue());
   }

   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default ByteShortPair right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteShortPair right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default ByteShortPair second(short r) {
      return this.right(r);
   }

   @Deprecated
   default ByteShortPair second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default ByteShortPair value(short r) {
      return this.right(r);
   }

   @Deprecated
   default ByteShortPair value(Short l) {
      return this.value(l.shortValue());
   }

   static ByteShortPair of(byte left, short right) {
      return new ByteShortImmutablePair(left, right);
   }

   static Comparator<ByteShortPair> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
