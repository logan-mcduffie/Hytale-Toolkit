package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;

public interface CharReferencePair<V> extends Pair<Character, V> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharReferencePair<V> left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharReferencePair<V> left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharReferencePair<V> first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharReferencePair<V> first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharReferencePair<V> key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharReferencePair<V> key(Character l) {
      return this.key(l.charValue());
   }

   static <V> CharReferencePair<V> of(char left, V right) {
      return new CharReferenceImmutablePair<>(left, right);
   }
}
