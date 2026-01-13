package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharCharPair extends Pair<Character, Character> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharCharPair left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharCharPair left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharCharPair first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharCharPair first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharCharPair key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharCharPair key(Character l) {
      return this.key(l.charValue());
   }

   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default CharCharPair right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharCharPair right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default CharCharPair second(char r) {
      return this.right(r);
   }

   @Deprecated
   default CharCharPair second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default CharCharPair value(char r) {
      return this.right(r);
   }

   @Deprecated
   default CharCharPair value(Character l) {
      return this.value(l.charValue());
   }

   static CharCharPair of(char left, char right) {
      return new CharCharImmutablePair(left, right);
   }

   static Comparator<CharCharPair> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
