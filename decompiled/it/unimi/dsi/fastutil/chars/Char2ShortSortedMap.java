package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2ShortSortedMap extends Char2ShortMap, SortedMap<Character, Short> {
   Char2ShortSortedMap subMap(char var1, char var2);

   Char2ShortSortedMap headMap(char var1);

   Char2ShortSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2ShortSortedMap subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2ShortSortedMap headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2ShortSortedMap tailMap(Character from) {
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
   default ObjectSortedSet<Entry<Character, Short>> entrySet() {
      return this.char2ShortEntrySet();
   }

   ObjectSortedSet<Char2ShortMap.Entry> char2ShortEntrySet();

   CharSortedSet keySet();

   @Override
   ShortCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2ShortMap.Entry>, Char2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2ShortMap.Entry> fastIterator(Char2ShortMap.Entry var1);
   }
}
