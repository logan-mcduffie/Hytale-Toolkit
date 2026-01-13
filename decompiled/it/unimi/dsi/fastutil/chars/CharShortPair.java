package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharShortPair extends Pair<Character, Short> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharShortPair left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharShortPair left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharShortPair first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharShortPair first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharShortPair key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharShortPair key(Character l) {
      return this.key(l.charValue());
   }

   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default CharShortPair right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharShortPair right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default CharShortPair second(short r) {
      return this.right(r);
   }

   @Deprecated
   default CharShortPair second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default CharShortPair value(short r) {
      return this.right(r);
   }

   @Deprecated
   default CharShortPair value(Short l) {
      return this.value(l.shortValue());
   }

   static CharShortPair of(char left, short right) {
      return new CharShortImmutablePair(left, right);
   }

   static Comparator<CharShortPair> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
