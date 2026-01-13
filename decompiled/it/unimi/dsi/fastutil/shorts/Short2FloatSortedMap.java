package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2FloatSortedMap extends Short2FloatMap, SortedMap<Short, Float> {
   Short2FloatSortedMap subMap(short var1, short var2);

   Short2FloatSortedMap headMap(short var1);

   Short2FloatSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2FloatSortedMap subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2FloatSortedMap headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2FloatSortedMap tailMap(Short from) {
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
   default ObjectSortedSet<Entry<Short, Float>> entrySet() {
      return this.short2FloatEntrySet();
   }

   ObjectSortedSet<Short2FloatMap.Entry> short2FloatEntrySet();

   ShortSortedSet keySet();

   @Override
   FloatCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2FloatMap.Entry>, Short2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2FloatMap.Entry> fastIterator(Short2FloatMap.Entry var1);
   }
}
