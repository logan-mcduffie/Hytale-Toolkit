package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2CharSortedMap extends Short2CharMap, SortedMap<Short, Character> {
   Short2CharSortedMap subMap(short var1, short var2);

   Short2CharSortedMap headMap(short var1);

   Short2CharSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2CharSortedMap subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2CharSortedMap headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2CharSortedMap tailMap(Short from) {
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
   default ObjectSortedSet<Entry<Short, Character>> entrySet() {
      return this.short2CharEntrySet();
   }

   ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet();

   ShortSortedSet keySet();

   @Override
   CharCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2CharMap.Entry>, Short2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2CharMap.Entry> fastIterator(Short2CharMap.Entry var1);
   }
}
