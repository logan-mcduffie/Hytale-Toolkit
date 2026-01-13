package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2ObjectSortedMap<V> extends Long2ObjectMap<V>, SortedMap<Long, V> {
   Long2ObjectSortedMap<V> subMap(long var1, long var3);

   Long2ObjectSortedMap<V> headMap(long var1);

   Long2ObjectSortedMap<V> tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2ObjectSortedMap<V> subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2ObjectSortedMap<V> headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2ObjectSortedMap<V> tailMap(Long from) {
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
      return this.long2ObjectEntrySet();
   }

   ObjectSortedSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet();

   LongSortedSet keySet();

   @Override
   ObjectCollection<V> values();

   LongComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Long2ObjectMap.Entry<V>>, Long2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> fastIterator(Long2ObjectMap.Entry<V> var1);
   }
}
