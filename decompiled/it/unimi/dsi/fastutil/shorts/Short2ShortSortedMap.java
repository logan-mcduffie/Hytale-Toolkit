package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2ShortSortedMap extends Short2ShortMap, SortedMap<Short, Short> {
   Short2ShortSortedMap subMap(short var1, short var2);

   Short2ShortSortedMap headMap(short var1);

   Short2ShortSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2ShortSortedMap subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2ShortSortedMap headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2ShortSortedMap tailMap(Short from) {
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
   default ObjectSortedSet<Entry<Short, Short>> entrySet() {
      return this.short2ShortEntrySet();
   }

   ObjectSortedSet<Short2ShortMap.Entry> short2ShortEntrySet();

   ShortSortedSet keySet();

   @Override
   ShortCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2ShortMap.Entry>, Short2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2ShortMap.Entry> fastIterator(Short2ShortMap.Entry var1);
   }
}
