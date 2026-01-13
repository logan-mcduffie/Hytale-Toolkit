package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Int2CharSortedMap extends Int2CharMap, SortedMap<Integer, Character> {
   Int2CharSortedMap subMap(int var1, int var2);

   Int2CharSortedMap headMap(int var1);

   Int2CharSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   @Deprecated
   default Int2CharSortedMap subMap(Integer from, Integer to) {
      return this.subMap(from.intValue(), to.intValue());
   }

   @Deprecated
   default Int2CharSortedMap headMap(Integer to) {
      return this.headMap(to.intValue());
   }

   @Deprecated
   default Int2CharSortedMap tailMap(Integer from) {
      return this.tailMap(from.intValue());
   }

   @Deprecated
   default Integer firstKey() {
      return this.firstIntKey();
   }

   @Deprecated
   default Integer lastKey() {
      return this.lastIntKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Integer, Character>> entrySet() {
      return this.int2CharEntrySet();
   }

   ObjectSortedSet<Int2CharMap.Entry> int2CharEntrySet();

   IntSortedSet keySet();

   @Override
   CharCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2CharMap.Entry>, Int2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2CharMap.Entry> fastIterator(Int2CharMap.Entry var1);
   }
}
