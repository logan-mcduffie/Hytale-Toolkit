package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2DoubleSortedMap extends Long2DoubleMap, SortedMap<Long, Double> {
   Long2DoubleSortedMap subMap(long var1, long var3);

   Long2DoubleSortedMap headMap(long var1);

   Long2DoubleSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2DoubleSortedMap subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2DoubleSortedMap headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2DoubleSortedMap tailMap(Long from) {
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
   default ObjectSortedSet<Entry<Long, Double>> entrySet() {
      return this.long2DoubleEntrySet();
   }

   ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet();

   LongSortedSet keySet();

   @Override
   DoubleCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2DoubleMap.Entry>, Long2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator(Long2DoubleMap.Entry var1);
   }
}
