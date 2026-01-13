package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SortedPair;
import java.io.Serializable;

public interface CharCharSortedPair extends CharCharPair, SortedPair<Character>, Serializable {
   static CharCharSortedPair of(char left, char right) {
      return CharCharImmutableSortedPair.of(left, right);
   }

   default boolean contains(char e) {
      return e == this.leftChar() || e == this.rightChar();
   }

   @Deprecated
   @Override
   default boolean contains(Object o) {
      return o == null ? false : this.contains(((Character)o).charValue());
   }
}
