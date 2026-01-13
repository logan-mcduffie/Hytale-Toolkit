package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ObjectCharPair<K> extends Pair<K, Character> {
   char rightChar();

   @Deprecated
   default Character right() {
      return this.rightChar();
   }

   default ObjectCharPair<K> right(char r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ObjectCharPair<K> right(Character l) {
      return this.right(l.charValue());
   }

   default char secondChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character second() {
      return this.secondChar();
   }

   default ObjectCharPair<K> second(char r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectCharPair<K> second(Character l) {
      return this.second(l.charValue());
   }

   default char valueChar() {
      return this.rightChar();
   }

   @Deprecated
   default Character value() {
      return this.valueChar();
   }

   default ObjectCharPair<K> value(char r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectCharPair<K> value(Character l) {
      return this.value(l.charValue());
   }

   static <K> ObjectCharPair<K> of(K left, char right) {
      return new ObjectCharImmutablePair<>(left, right);
   }

   static <K> Comparator<ObjectCharPair<K>> lexComparator() {
      return (x, y) -> {
         int t = ((Comparable)x.left()).compareTo(y.left());
         return t != 0 ? t : Character.compare(x.rightChar(), y.rightChar());
      };
   }
}
