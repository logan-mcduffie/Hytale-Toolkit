package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2ReferenceSortedMap<V> extends Long2ReferenceMap<V>, SortedMap<Long, V> {
   Long2ReferenceSortedMap<V> subMap(long var1, long var3);

   Long2ReferenceSortedMap<V> headMap(long var1);

   Long2ReferenceSortedMap<V> tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2ReferenceSortedMap<V> subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2ReferenceSortedMap<V> headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2ReferenceSortedMap<V> tailMap(Long from) {
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
   default ObjectSortedSet<Entry<Long, V>> entrySet() {
      return this.long2ReferenceEntrySet();
   }

   ObjectSortedSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet();

   LongSortedSet keySet();

   @Override
   ReferenceCollection<V> values();

   LongComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Long2ReferenceMap.Entry<V>>, Long2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> fastIterator(Long2ReferenceMap.Entry<V> var1);
   }
}
