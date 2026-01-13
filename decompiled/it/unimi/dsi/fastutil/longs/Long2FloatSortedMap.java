package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2FloatSortedMap extends Long2FloatMap, SortedMap<Long, Float> {
   Long2FloatSortedMap subMap(long var1, long var3);

   Long2FloatSortedMap headMap(long var1);

   Long2FloatSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2FloatSortedMap subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2FloatSortedMap headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2FloatSortedMap tailMap(Long from) {
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
   default ObjectSortedSet<Entry<Long, Float>> entrySet() {
      return this.long2FloatEntrySet();
   }

   ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet();

   LongSortedSet keySet();

   @Override
   FloatCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2FloatMap.Entry>, Long2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2FloatMap.Entry> fastIterator(Long2FloatMap.Entry var1);
   }
}
