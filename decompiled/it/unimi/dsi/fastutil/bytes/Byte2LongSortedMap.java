package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2LongSortedMap extends Byte2LongMap, SortedMap<Byte, Long> {
   Byte2LongSortedMap subMap(byte var1, byte var2);

   Byte2LongSortedMap headMap(byte var1);

   Byte2LongSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2LongSortedMap subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2LongSortedMap headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2LongSortedMap tailMap(Byte from) {
      return this.tailMap(from.byteValue());
   }

   @Deprecated
   default Byte firstKey() {
      return this.firstByteKey();
   }

   @Deprecated
   default Byte lastKey() {
      return this.lastByteKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Byte, Long>> entrySet() {
      return this.byte2LongEntrySet();
   }

   ObjectSortedSet<Byte2LongMap.Entry> byte2LongEntrySet();

   ByteSortedSet keySet();

   @Override
   LongCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2LongMap.Entry>, Byte2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2LongMap.Entry> fastIterator(Byte2LongMap.Entry var1);
   }
}
