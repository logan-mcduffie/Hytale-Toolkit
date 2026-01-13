package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface IntDoublePair extends Pair<Integer, Double> {
   int leftInt();

   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntDoublePair left(int l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntDoublePair left(Integer l) {
      return this.left(l.intValue());
   }

   default int firstInt() {
      return this.leftInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntDoublePair first(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntDoublePair first(Integer l) {
      return this.first(l.intValue());
   }

   default int keyInt() {
      return this.firstInt();
   }

   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntDoublePair key(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntDoublePair key(Integer l) {
      return this.key(l.intValue());
   }

   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default IntDoublePair right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntDoublePair right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default IntDoublePair second(double r) {
      return this.right(r);
   }

   @Deprecated
   default IntDoublePair second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default IntDoublePair value(double r) {
      return this.right(r);
   }

   @Deprecated
   default IntDoublePair value(Double l) {
      return this.value(l.doubleValue());
   }

   static IntDoublePair of(int left, double right) {
      return new IntDoubleImmutablePair(left, right);
   }

   static Comparator<IntDoublePair> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
