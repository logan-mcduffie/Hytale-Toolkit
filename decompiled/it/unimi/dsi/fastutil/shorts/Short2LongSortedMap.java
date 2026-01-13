package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2LongSortedMap extends Short2LongMap, SortedMap<Short, Long> {
   Short2LongSortedMap subMap(short var1, short var2);

   Short2LongSortedMap headMap(short var1);

   Short2LongSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2LongSortedMap subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2LongSortedMap headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2LongSortedMap tailMap(Short from) {
      return this.tailMap(from.shortValue());
   }

   @Deprecated
   default Short firstKey() {
      return this.firstShortKey();
   }

   @Deprecated
   default Short lastKey() {
      return this.lastShortKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Short, Long>> entrySet() {
      return this.short2LongEntrySet();
   }

   ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet();

   ShortSortedSet keySet();

   @Override
   LongCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2LongMap.Entry>, Short2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2LongMap.Entry> fastIterator(Short2LongMap.Entry var1);
   }
}
