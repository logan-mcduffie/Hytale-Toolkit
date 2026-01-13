package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2LongSortedMap extends Long2LongMap, SortedMap<Long, Long> {
   Long2LongSortedMap subMap(long var1, long var3);

   Long2LongSortedMap headMap(long var1);

   Long2LongSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2LongSortedMap subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2LongSortedMap headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2LongSortedMap tailMap(Long from) {
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
   default ObjectSortedSet<Entry<Long, Long>> entrySet() {
      return this.long2LongEntrySet();
   }

   ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet();

   LongSortedSet keySet();

   @Override
   LongCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2LongMap.Entry>, Long2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2LongMap.Entry> fastIterator(Long2LongMap.Entry var1);
   }
}
