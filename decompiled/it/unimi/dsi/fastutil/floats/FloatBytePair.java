package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatBytePair extends Pair<Float, Byte> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatBytePair left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatBytePair left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatBytePair first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatBytePair first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatBytePair key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatBytePair key(Float l) {
      return this.key(l.floatValue());
   }

   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default FloatBytePair right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatBytePair right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default FloatBytePair second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default FloatBytePair second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default FloatBytePair value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default FloatBytePair value(Byte l) {
      return this.value(l.byteValue());
   }

   static FloatBytePair of(float left, byte right) {
      return new FloatByteImmutablePair(left, right);
   }

   static Comparator<FloatBytePair> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
