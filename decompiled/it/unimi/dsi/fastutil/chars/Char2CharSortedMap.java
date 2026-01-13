package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2CharSortedMap extends Char2CharMap, SortedMap<Character, Character> {
   Char2CharSortedMap subMap(char var1, char var2);

   Char2CharSortedMap headMap(char var1);

   Char2CharSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2CharSortedMap subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2CharSortedMap headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2CharSortedMap tailMap(Character from) {
      return this.tailMap(from.charValue());
   }

   @Deprecated
   default Character firstKey() {
      return this.firstCharKey();
   }

   @Deprecated
   default Character lastKey() {
      return this.lastCharKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Character, Character>> entrySet() {
      return this.char2CharEntrySet();
   }

   ObjectSortedSet<Char2CharMap.Entry> char2CharEntrySet();

   CharSortedSet keySet();

   @Override
   CharCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2CharMap.Entry>, Char2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2CharMap.Entry> fastIterator(Char2CharMap.Entry var1);
   }
}
