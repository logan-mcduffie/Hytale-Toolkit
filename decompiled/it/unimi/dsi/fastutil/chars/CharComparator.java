package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface CharComparator extends Comparator<Character> {
   int compare(char var1, char var2);

   default CharComparator reversed() {
      return CharComparators.oppositeComparator(this);
   }

   @Deprecated
   default int compare(Character ok1, Character ok2) {
      return this.compare(ok1.charValue(), ok2.charValue());
   }

   default CharComparator thenComparing(CharComparator second) {
      return (CharComparator)((Serializable)((k1, k2) -> {
         int comp = this.compare(k1, k2);
         return comp == 0 ? second.compare(k1, k2) : comp;
      }));
   }

   @Override
   default Comparator<Character> thenComparing(Comparator<? super Character> second) {
      return (Comparator<Character>)(second instanceof CharComparator ? this.thenComparing((CharComparator)second) : Comparator.super.thenComparing(second));
   }
}
