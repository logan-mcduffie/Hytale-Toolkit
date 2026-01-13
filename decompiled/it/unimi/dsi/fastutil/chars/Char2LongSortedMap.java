package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2LongSortedMap extends Char2LongMap, SortedMap<Character, Long> {
   Char2LongSortedMap subMap(char var1, char var2);

   Char2LongSortedMap headMap(char var1);

   Char2LongSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2LongSortedMap subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2LongSortedMap headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2LongSortedMap tailMap(Character from) {
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
   default ObjectSortedSet<Entry<Character, Long>> entrySet() {
      return this.char2LongEntrySet();
   }

   ObjectSortedSet<Char2LongMap.Entry> char2LongEntrySet();

   CharSortedSet keySet();

   @Override
   LongCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2LongMap.Entry>, Char2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2LongMap.Entry> fastIterator(Char2LongMap.Entry var1);
   }
}
