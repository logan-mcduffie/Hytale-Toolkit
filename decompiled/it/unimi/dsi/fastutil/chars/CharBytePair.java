package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharBytePair extends Pair<Character, Byte> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharBytePair left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharBytePair left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharBytePair first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharBytePair first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharBytePair key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharBytePair key(Character l) {
      return this.key(l.charValue());
   }

   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default CharBytePair right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharBytePair right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default CharBytePair second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default CharBytePair second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default CharBytePair value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default CharBytePair value(Byte l) {
      return this.value(l.byteValue());
   }

   static CharBytePair of(char left, byte right) {
      return new CharByteImmutablePair(left, right);
   }

   static Comparator<CharBytePair> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
