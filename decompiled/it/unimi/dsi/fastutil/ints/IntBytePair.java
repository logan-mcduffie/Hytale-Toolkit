package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface IntBytePair extends Pair<Integer, Byte> {
   int leftInt();

   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntBytePair left(int l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntBytePair left(Integer l) {
      return this.left(l.intValue());
   }

   default int firstInt() {
      return this.leftInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntBytePair first(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntBytePair first(Integer l) {
      return this.first(l.intValue());
   }

   default int keyInt() {
      return this.firstInt();
   }

   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntBytePair key(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntBytePair key(Integer l) {
      return this.key(l.intValue());
   }

   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default IntBytePair right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntBytePair right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default IntBytePair second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default IntBytePair second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default IntBytePair value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default IntBytePair value(Byte l) {
      return this.value(l.byteValue());
   }

   static IntBytePair of(int left, byte right) {
      return new IntByteImmutablePair(left, right);
   }

   static Comparator<IntBytePair> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
