package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface DoubleCharPair extends Pair<Double, Character> {
   double leftDouble();

   @Deprecated
   default Double left() {
      return this.leftDouble();
   }

   default DoubleCharPair left(double l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleCharPair left(Double l) {
      return this.left(l.doubleValue());
   }

   default double firstDouble() {
      return this.leftDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   default DoubleCharPair first(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleCharPair first(Double l) {
      return this.first(l.doubleValue());
   }

   default double keyDouble() {
      return this.firstDouble();
   }

   @Deprecated
   default Double key() {
      return this.keyDouble();
   }

   default DoubleCharPair key(double l) {
      return this.left(l);
   }

   @Deprecated
   default DoubleCharPair key(Double l) {
      return this.key(l.doubleValue());
   }

   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default DoubleCharPair right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default DoubleCharPair right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default DoubleCharPair second(char r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleCharPair second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default DoubleCharPair value(char r) {
      return this.right(r);
   }

   @Deprecated
   default DoubleCharPair value(Character l) {
      return this.value(l.charValue());
   }

   static DoubleCharPair of(double left, char right) {
      return new DoubleCharImmutablePair(left, right);
   }

   static Comparator<DoubleCharPair> lexComparator() {
      return (x, y) -> {
         int t = Double.compare(x.leftDouble(), y.leftDouble());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
