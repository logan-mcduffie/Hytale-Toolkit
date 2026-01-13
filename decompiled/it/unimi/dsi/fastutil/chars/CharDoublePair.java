package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharDoublePair extends Pair<Character, Double> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharDoublePair left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharDoublePair left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharDoublePair first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharDoublePair first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharDoublePair key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharDoublePair key(Character l) {
      return this.key(l.charValue());
   }

   double rightDouble();

   @Deprecated
   default Double right() {
      return this.rightDouble();
   }

   default CharDoublePair right(double r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharDoublePair right(Double l) {
      return this.right(l.doubleValue());
   }

   default double secondDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double second() {
      return this.secondDouble();
   }

   default CharDoublePair second(double r) {
      return this.right(r);
   }

   @Deprecated
   default CharDoublePair second(Double l) {
      return this.second(l.doubleValue());
   }

   default double valueDouble() {
      return this.rightDouble();
   }

   @Deprecated
   default Double value() {
      return this.valueDouble();
   }

   default CharDoublePair value(double r) {
      return this.right(r);
   }

   @Deprecated
   default CharDoublePair value(Double l) {
      return this.value(l.doubleValue());
   }

   static CharDoublePair of(char left, double right) {
      return new CharDoubleImmutablePair(left, right);
   }

   static Comparator<CharDoublePair> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : Double.compare(x.rightDouble(), y.rightDouble());
      };
   }
}
