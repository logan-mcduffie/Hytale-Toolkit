package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2DoubleSortedMap extends Short2DoubleMap, SortedMap<Short, Double> {
   Short2DoubleSortedMap subMap(short var1, short var2);

   Short2DoubleSortedMap headMap(short var1);

   Short2DoubleSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2DoubleSortedMap subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2DoubleSortedMap headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2DoubleSortedMap tailMap(Short from) {
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
   default ObjectSortedSet<Entry<Short, Double>> entrySet() {
      return this.short2DoubleEntrySet();
   }

   ObjectSortedSet<Short2DoubleMap.Entry> short2DoubleEntrySet();

   ShortSortedSet keySet();

   @Override
   DoubleCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2DoubleMap.Entry>, Short2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2DoubleMap.Entry> fastIterator(Short2DoubleMap.Entry var1);
   }
}
