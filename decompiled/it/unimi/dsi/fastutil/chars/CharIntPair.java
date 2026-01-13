package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharIntPair extends Pair<Character, Integer> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharIntPair left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharIntPair left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharIntPair first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharIntPair first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharIntPair key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharIntPair key(Character l) {
      return this.key(l.charValue());
   }

   int rightInt();

   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default CharIntPair right(int r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharIntPair right(Integer l) {
      return this.right(l.intValue());
   }

   default int secondInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default CharIntPair second(int r) {
      return this.right(r);
   }

   @Deprecated
   default CharIntPair second(Integer l) {
      return this.second(l.intValue());
   }

   default int valueInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default CharIntPair value(int r) {
      return this.right(r);
   }

   @Deprecated
   default CharIntPair value(Integer l) {
      return this.value(l.intValue());
   }

   static CharIntPair of(char left, int right) {
      return new CharIntImmutablePair(left, right);
   }

   static Comparator<CharIntPair> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
