package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortBytePair extends Pair<Short, Byte> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortBytePair left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortBytePair left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortBytePair first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortBytePair first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortBytePair key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortBytePair key(Short l) {
      return this.key(l.shortValue());
   }

   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default ShortBytePair right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortBytePair right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default ShortBytePair second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default ShortBytePair second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default ShortBytePair value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default ShortBytePair value(Byte l) {
      return this.value(l.byteValue());
   }

   static ShortBytePair of(short left, byte right) {
      return new ShortByteImmutablePair(left, right);
   }

   static Comparator<ShortBytePair> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
