package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteDoublePair extends Pair<Byte, Double> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteDoublePair left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteDoublePair left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteDoublePair first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteDoublePair first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteDoublePair key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteDoublePair key(Byte l) {
      return this.key(l.byteValue());
   }

   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default ByteDoublePair right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteDoublePair right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default ByteDoublePair second(double r) {
      return this.right(r);
   }

   @Deprecated
   default ByteDoublePair second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default ByteDoublePair value(double r) {
      return this.right(r);
   }

   @Deprecated
   default ByteDoublePair value(Double l) {
      return this.value(l.doubleValue());
   }

   static ByteDoublePair of(byte left, double right) {
      return new ByteDoubleImmutablePair(left, right);
   }

   static Comparator<ByteDoublePair> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
