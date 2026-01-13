package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2IntSortedMap extends Char2IntMap, SortedMap<Character, Integer> {
   Char2IntSortedMap subMap(char var1, char var2);

   Char2IntSortedMap headMap(char var1);

   Char2IntSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2IntSortedMap subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2IntSortedMap headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2IntSortedMap tailMap(Character from) {
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
   default ObjectSortedSet<Entry<Character, Integer>> entrySet() {
      return this.char2IntEntrySet();
   }

   ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet();

   CharSortedSet keySet();

   @Override
   IntCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2IntMap.Entry>, Char2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2IntMap.Entry> fastIterator(Char2IntMap.Entry var1);
   }
}
