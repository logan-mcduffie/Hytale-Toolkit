package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2IntSortedMap extends Short2IntMap, SortedMap<Short, Integer> {
   Short2IntSortedMap subMap(short var1, short var2);

   Short2IntSortedMap headMap(short var1);

   Short2IntSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2IntSortedMap subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2IntSortedMap headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2IntSortedMap tailMap(Short from) {
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
   default ObjectSortedSet<Entry<Short, Integer>> entrySet() {
      return this.short2IntEntrySet();
   }

   ObjectSortedSet<Short2IntMap.Entry> short2IntEntrySet();

   ShortSortedSet keySet();

   @Override
   IntCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2IntMap.Entry>, Short2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2IntMap.Entry> fastIterator(Short2IntMap.Entry var1);
   }
}
