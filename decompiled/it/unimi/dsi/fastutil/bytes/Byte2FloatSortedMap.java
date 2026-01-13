package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2FloatSortedMap extends Byte2FloatMap, SortedMap<Byte, Float> {
   Byte2FloatSortedMap subMap(byte var1, byte var2);

   Byte2FloatSortedMap headMap(byte var1);

   Byte2FloatSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2FloatSortedMap subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2FloatSortedMap headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2FloatSortedMap tailMap(Byte from) {
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
   default ObjectSortedSet<Entry<Byte, Float>> entrySet() {
      return this.byte2FloatEntrySet();
   }

   ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet();

   ByteSortedSet keySet();

   @Override
   FloatCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2FloatMap.Entry>, Byte2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2FloatMap.Entry> fastIterator(Byte2FloatMap.Entry var1);
   }
}
