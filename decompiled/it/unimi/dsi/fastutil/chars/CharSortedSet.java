package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Size64;
import java.util.SortedSet;

public interface CharSortedSet extends CharSet, SortedSet<Character>, CharBidirectionalIterable {
   CharBidirectionalIterator iterator(char var1);

   @Override
   CharBidirectionalIterator iterator();

   @Override
   default CharSpliterator spliterator() {
      return CharSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
   }

   CharSortedSet subSet(char var1, char var2);

   CharSortedSet headSet(char var1);

   CharSortedSet tailSet(char var1);

   CharComparator comparator();

   char firstChar();

   char lastChar();

   @Deprecated
   default CharSortedSet subSet(Character from, Character to) {
      return this.subSet(from.charValue(), to.charValue());
   }

   @Deprecated
   default CharSortedSet headSet(Character to) {
      return this.headSet(to.charValue());
   }

   @Deprecated
   default CharSortedSet tailSet(Character from) {
      return this.tailSet(from.charValue());
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   @Deprecated
   default Character last() {
      return this.lastChar();
   }
}
