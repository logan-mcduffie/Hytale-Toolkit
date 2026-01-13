package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortCharPair extends Pair<Short, Character> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortCharPair left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortCharPair left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortCharPair first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortCharPair first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortCharPair key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortCharPair key(Short l) {
      return this.key(l.shortValue());
   }

   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default ShortCharPair right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortCharPair right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default ShortCharPair second(char r) {
      return this.right(r);
   }

   @Deprecated
   default ShortCharPair second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default ShortCharPair value(char r) {
      return this.right(r);
   }

   @Deprecated
   default ShortCharPair value(Character l) {
      return this.value(l.charValue());
   }

   static ShortCharPair of(short left, char right) {
      return new ShortCharImmutablePair(left, right);
   }

   static Comparator<ShortCharPair> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
