package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2ByteSortedMap extends Short2ByteMap, SortedMap<Short, Byte> {
   Short2ByteSortedMap subMap(short var1, short var2);

   Short2ByteSortedMap headMap(short var1);

   Short2ByteSortedMap tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2ByteSortedMap subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2ByteSortedMap headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2ByteSortedMap tailMap(Short from) {
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
   default ObjectSortedSet<Entry<Short, Byte>> entrySet() {
      return this.short2ByteEntrySet();
   }

   ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet();

   ShortSortedSet keySet();

   @Override
   ByteCollection values();

   ShortComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Short2ByteMap.Entry>, Short2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator(Short2ByteMap.Entry var1);
   }
}
