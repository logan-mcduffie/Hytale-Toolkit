package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceCharPair<K> extends Pair<K, Character> {
   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default ReferenceCharPair<K> right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ReferenceCharPair<K> right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default ReferenceCharPair<K> second(char r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceCharPair<K> second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default ReferenceCharPair<K> value(char r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceCharPair<K> value(Character l) {
      return this.value(l.charValue());
   }

   static <K> ReferenceCharPair<K> of(K left, char right) {
      return new ReferenceCharImmutablePair<>(left, right);
   }
}
