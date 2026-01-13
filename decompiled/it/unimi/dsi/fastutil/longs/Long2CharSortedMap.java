package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Long2CharSortedMap extends Long2CharMap, SortedMap<Long, Character> {
   Long2CharSortedMap subMap(long var1, long var3);

   Long2CharSortedMap headMap(long var1);

   Long2CharSortedMap tailMap(long var1);

   long firstLongKey();

   long lastLongKey();

   @Deprecated
   default Long2CharSortedMap subMap(Long from, Long to) {
      return this.subMap(from.longValue(), to.longValue());
   }

   @Deprecated
   default Long2CharSortedMap headMap(Long to) {
      return this.headMap(to.longValue());
   }

   @Deprecated
   default Long2CharSortedMap tailMap(Long from) {
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
   default ObjectSortedSet<Entry<Long, Character>> entrySet() {
      return this.long2CharEntrySet();
   }

   ObjectSortedSet<Long2CharMap.Entry> long2CharEntrySet();

   LongSortedSet keySet();

   @Override
   CharCollection values();

   LongComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Long2CharMap.Entry>, Long2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Long2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Long2CharMap.Entry> fastIterator(Long2CharMap.Entry var1);
   }
}
