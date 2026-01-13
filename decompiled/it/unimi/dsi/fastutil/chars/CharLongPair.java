package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharLongPair extends Pair<Character, Long> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharLongPair left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharLongPair left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharLongPair first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharLongPair first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharLongPair key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharLongPair key(Character l) {
      return this.key(l.charValue());
   }

   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default CharLongPair right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharLongPair right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default CharLongPair second(long r) {
      return this.right(r);
   }

   @Deprecated
   default CharLongPair second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default CharLongPair value(long r) {
      return this.right(r);
   }

   @Deprecated
   default CharLongPair value(Long l) {
      return this.value(l.longValue());
   }

   static CharLongPair of(char left, long right) {
      return new CharLongImmutablePair(left, right);
   }

   static Comparator<CharLongPair> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : Long.compare(x.rightLong(), y.rightLong());
      };
   }
}
