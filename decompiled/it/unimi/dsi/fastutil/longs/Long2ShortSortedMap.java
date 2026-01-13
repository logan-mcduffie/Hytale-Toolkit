package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2ShortSortedMap extends Long2ShortMap, SortedMap<Long, Short> {
   Long2ShortSortedMap subMap(long var1, long var3);

   Long2ShortSortedMap headMap(long var1);

   Long2ShortSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2ShortSortedMap subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2ShortSortedMap headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2ShortSortedMap tailMap(Long from) {
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
   default ObjectSortedSet<Entry<Long, Short>> entrySet() {
      return this.long2ShortEntrySet();
   }

   ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet();

   LongSortedSet keySet();

   @Override
   ShortCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2ShortMap.Entry>, Long2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2ShortMap.Entry> fastIterator(Long2ShortMap.Entry var1);
   }
}
