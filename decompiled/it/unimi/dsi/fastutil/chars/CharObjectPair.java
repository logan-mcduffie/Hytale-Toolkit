package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface CharObjectPair<V> extends Pair<Character, V> {
   char leftChar();

   @Deprecated
   default Character left() {
      return this.leftChar();
   }

   default CharObjectPair<V> left(char l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default CharObjectPair<V> left(Character l) {
      return this.left(l.charValue());
   }

   default char firstChar() {
      return this.leftChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   default CharObjectPair<V> first(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharObjectPair<V> first(Character l) {
      return this.first(l.charValue());
   }

   default char keyChar() {
      return this.firstChar();
   }

   @Deprecated
   default Character key() {
      return this.keyChar();
   }

   default CharObjectPair<V> key(char l) {
      return this.left(l);
   }

   @Deprecated
   default CharObjectPair<V> key(Character l) {
      return this.key(l.charValue());
   }

   static <V> CharObjectPair<V> of(char left, V right) {
      return new CharObjectImmutablePair<>(left, right);
   }

   static <V> Comparator<CharObjectPair<V>> lexComparator() {
      return (x, y) -> {
         int t = Character.compare(x.leftChar(), y.leftChar());
         return t != 0 ? t : ((Comparable)x.right()).compareTo(y.right());
      };
   }
}
