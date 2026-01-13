package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2ByteSortedMap extends Byte2ByteMap, SortedMap<Byte, Byte> {
   Byte2ByteSortedMap subMap(byte var1, byte var2);

   Byte2ByteSortedMap headMap(byte var1);

   Byte2ByteSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2ByteSortedMap subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2ByteSortedMap headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2ByteSortedMap tailMap(Byte from) {
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
   default ObjectSortedSet<Entry<Byte, Byte>> entrySet() {
      return this.byte2ByteEntrySet();
   }

   ObjectSortedSet<Byte2ByteMap.Entry> byte2ByteEntrySet();

   ByteSortedSet keySet();

   @Override
   ByteCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2ByteMap.Entry>, Byte2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2ByteMap.Entry> fastIterator(Byte2ByteMap.Entry var1);
   }
}
