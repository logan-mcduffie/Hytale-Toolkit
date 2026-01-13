package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2FloatSortedMap extends Char2FloatMap, SortedMap<Character, Float> {
   Char2FloatSortedMap subMap(char var1, char var2);

   Char2FloatSortedMap headMap(char var1);

   Char2FloatSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2FloatSortedMap subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2FloatSortedMap headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2FloatSortedMap tailMap(Character from) {
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
   default ObjectSortedSet<Entry<Character, Float>> entrySet() {
      return this.char2FloatEntrySet();
   }

   ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet();

   CharSortedSet keySet();

   @Override
   FloatCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2FloatMap.Entry>, Char2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2FloatMap.Entry> fastIterator(Char2FloatMap.Entry var1);
   }
}
