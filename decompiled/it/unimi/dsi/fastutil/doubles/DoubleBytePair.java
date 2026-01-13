package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleBytePair extends Pair<Double, Byte> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleBytePair left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleBytePair left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleBytePair first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleBytePair first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleBytePair key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleBytePair key(Double l) {
      return this.key(l.doubleValue());
   }

   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default DoubleBytePair right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleBytePair right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default DoubleBytePair second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleBytePair second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default DoubleBytePair value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleBytePair value(Byte l) {
      return this.value(l.byteValue());
   }

   static DoubleBytePair of(double left, byte right) {
      return new DoubleByteImmutablePair(left, right);
   }

   static Comparator<DoubleBytePair> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
