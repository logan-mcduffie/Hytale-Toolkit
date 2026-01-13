package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2ByteSortedMap extends Long2ByteMap, SortedMap<Long, Byte> {
   Long2ByteSortedMap subMap(long var1, long var3);

   Long2ByteSortedMap headMap(long var1);

   Long2ByteSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2ByteSortedMap subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2ByteSortedMap headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2ByteSortedMap tailMap(Long from) {
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
   default ObjectSortedSet<Entry<Long, Byte>> entrySet() {
      return this.long2ByteEntrySet();
   }

   ObjectSortedSet<Long2ByteMap.Entry> long2ByteEntrySet();

   LongSortedSet keySet();

   @Override
   ByteCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2ByteMap.Entry>, Long2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2ByteMap.Entry> fastIterator(Long2ByteMap.Entry var1);
   }
}
