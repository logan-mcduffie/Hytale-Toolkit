package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteIntPair extends Pair<Byte, Integer> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteIntPair left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteIntPair left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteIntPair first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteIntPair first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteIntPair key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteIntPair key(Byte l) {
      return this.key(l.byteValue());
   }

   int rightInt();

   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default ByteIntPair right(int r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteIntPair right(Integer l) {
      return this.right(l.intValue());
   }

   default int secondInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default ByteIntPair second(int r) {
      return this.right(r);
   }

   @Deprecated
   default ByteIntPair second(Integer l) {
      return this.second(l.intValue());
   }

   default int valueInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default ByteIntPair value(int r) {
      return this.right(r);
   }

   @Deprecated
   default ByteIntPair value(Integer l) {
      return this.value(l.intValue());
   }

   static ByteIntPair of(byte left, int right) {
      return new ByteIntImmutablePair(left, right);
   }

   static Comparator<ByteIntPair> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
