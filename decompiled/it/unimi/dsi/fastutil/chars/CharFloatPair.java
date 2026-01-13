package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharFloatPair extends Pair<Character, Float> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharFloatPair left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharFloatPair left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharFloatPair first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharFloatPair first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharFloatPair key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharFloatPair key(Character l) {
      return this.key(l.charValue());
   }

   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default CharFloatPair right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharFloatPair right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default CharFloatPair second(float r) {
      return this.right(r);
   }

   @Deprecated
   default CharFloatPair second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default CharFloatPair value(float r) {
      return this.right(r);
   }

   @Deprecated
   default CharFloatPair value(Float l) {
      return this.value(l.floatValue());
   }

   static CharFloatPair of(char left, float right) {
      return new CharFloatImmutablePair(left, right);
   }

   static Comparator<CharFloatPair> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
