package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharBooleanPair extends Pair<Character, Boolean> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharBooleanPair left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharBooleanPair left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharBooleanPair first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharBooleanPair first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharBooleanPair key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharBooleanPair key(Character l) {
      return this.key(l.charValue());
   }

   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default CharBooleanPair right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharBooleanPair right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default CharBooleanPair second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default CharBooleanPair second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default CharBooleanPair value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default CharBooleanPair value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static CharBooleanPair of(char left, boolean right) {
      return new CharBooleanImmutablePair(left, right);
   }

   static Comparator<CharBooleanPair> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
