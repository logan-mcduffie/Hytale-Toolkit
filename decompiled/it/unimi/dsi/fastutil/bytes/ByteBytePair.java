package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteBytePair extends Pair<Byte, Byte> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteBytePair left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteBytePair left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteBytePair first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteBytePair first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteBytePair key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteBytePair key(Byte l) {
      return this.key(l.byteValue());
   }

   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default ByteBytePair right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteBytePair right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default ByteBytePair second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default ByteBytePair second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default ByteBytePair value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default ByteBytePair value(Byte l) {
      return this.value(l.byteValue());
   }

   static ByteBytePair of(byte left, byte right) {
      return new ByteByteImmutablePair(left, right);
   }

   static Comparator<ByteBytePair> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
