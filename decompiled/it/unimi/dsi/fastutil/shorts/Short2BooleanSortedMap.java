package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2BooleanSortedMap extends Short2BooleanMap, SortedMap<Short, Boolean> {
   Short2BooleanSortedMap subMap(short var1, short var2);

   Short2BooleanSortedMap headMap(short var1);

   Short2BooleanSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2BooleanSortedMap subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2BooleanSortedMap headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2BooleanSortedMap tailMap(Short from) {
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
   default ObjectSortedSet<Entry<Short, Boolean>> entrySet() {
      return this.short2BooleanEntrySet();
   }

   ObjectSortedSet<Short2BooleanMap.Entry> short2BooleanEntrySet();

   ShortSortedSet keySet();

   @Override
   BooleanCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2BooleanMap.Entry>, Short2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2BooleanMap.Entry> fastIterator(Short2BooleanMap.Entry var1);
   }
}
