package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Int2LongSortedMap extends Int2LongMap, SortedMap<Integer, Long> {
   Int2LongSortedMap subMap(int var1, int var2);

   Int2LongSortedMap headMap(int var1);

   Int2LongSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   @Deprecated
   default Int2LongSortedMap subMap(Integer from, Integer to) {
      return this.subMap(from.intValue(), to.intValue());
   }

   @Deprecated
   default Int2LongSortedMap headMap(Integer to) {
      return this.headMap(to.intValue());
   }

   @Deprecated
   default Int2LongSortedMap tailMap(Integer from) {
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
   default ObjectSortedSet<Entry<Integer, Long>> entrySet() {
      return this.int2LongEntrySet();
   }

   ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet();

   IntSortedSet keySet();

   @Override
   LongCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2LongMap.Entry>, Int2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2LongMap.Entry> fastIterator(Int2LongMap.Entry var1);
   }
}
