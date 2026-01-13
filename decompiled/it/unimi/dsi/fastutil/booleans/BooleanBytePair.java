package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanBytePair extends Pair<Boolean, Byte> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanBytePair left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanBytePair left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanBytePair first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanBytePair first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanBytePair key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanBytePair key(Boolean l) {
      return this.key(l.booleanValue());
   }

   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default BooleanBytePair right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanBytePair right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default BooleanBytePair second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanBytePair second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default BooleanBytePair value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanBytePair value(Byte l) {
      return this.value(l.byteValue());
   }

   static BooleanBytePair of(boolean left, byte right) {
      return new BooleanByteImmutablePair(left, right);
   }

   static Comparator<BooleanBytePair> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
