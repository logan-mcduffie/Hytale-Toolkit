package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2IntSortedMap extends Byte2IntMap, SortedMap<Byte, Integer> {
   Byte2IntSortedMap subMap(byte var1, byte var2);

   Byte2IntSortedMap headMap(byte var1);

   Byte2IntSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2IntSortedMap subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2IntSortedMap headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2IntSortedMap tailMap(Byte from) {
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
   default ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
      return this.byte2IntEntrySet();
   }

   ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet();

   ByteSortedSet keySet();

   @Override
   IntCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2IntMap.Entry>, Byte2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator(Byte2IntMap.Entry var1);
   }
}
