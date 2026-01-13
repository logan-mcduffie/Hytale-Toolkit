package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2IntSortedMap extends Long2IntMap, SortedMap<Long, Integer> {
   Long2IntSortedMap subMap(long var1, long var3);

   Long2IntSortedMap headMap(long var1);

   Long2IntSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2IntSortedMap subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2IntSortedMap headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2IntSortedMap tailMap(Long from) {
      return this.tailMap(from.longValue());
   }

   @Deprecated
   default Long firstKey() {
      return this.firstLongKey();
   }

   @Deprecated
   default Long lastKey() {
      return this.lastLongKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Long, Integer>> entrySet() {
      return this.long2IntEntrySet();
   }

   ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet();

   LongSortedSet keySet();

   @Override
   IntCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2IntMap.Entry>, Long2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2IntMap.Entry> fastIterator(Long2IntMap.Entry var1);
   }
}
