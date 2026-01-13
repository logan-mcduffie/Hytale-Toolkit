package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2BooleanSortedMap extends Long2BooleanMap, SortedMap<Long, Boolean> {
   Long2BooleanSortedMap subMap(long var1, long var3);

   Long2BooleanSortedMap headMap(long var1);

   Long2BooleanSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2BooleanSortedMap subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2BooleanSortedMap headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2BooleanSortedMap tailMap(Long from) {
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
   default ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
      return this.long2BooleanEntrySet();
   }

   ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet();

   LongSortedSet keySet();

   @Override
   BooleanCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2BooleanMap.Entry>, Long2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2BooleanMap.Entry> fastIterator(Long2BooleanMap.Entry var1);
   }
}
