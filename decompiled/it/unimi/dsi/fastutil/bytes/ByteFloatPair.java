package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteFloatPair extends Pair<Byte, Float> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteFloatPair left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteFloatPair left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteFloatPair first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteFloatPair first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteFloatPair key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteFloatPair key(Byte l) {
      return this.key(l.byteValue());
   }

   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default ByteFloatPair right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteFloatPair right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default ByteFloatPair second(float r) {
      return this.right(r);
   }

   @Deprecated
   default ByteFloatPair second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default ByteFloatPair value(float r) {
      return this.right(r);
   }

   @Deprecated
   default ByteFloatPair value(Float l) {
      return this.value(l.floatValue());
   }

   static ByteFloatPair of(byte left, float right) {
      return new ByteFloatImmutablePair(left, right);
   }

   static Comparator<ByteFloatPair> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
