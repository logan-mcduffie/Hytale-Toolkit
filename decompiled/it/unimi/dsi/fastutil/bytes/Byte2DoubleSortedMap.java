package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2DoubleSortedMap extends Byte2DoubleMap, SortedMap<Byte, Double> {
   Byte2DoubleSortedMap subMap(byte var1, byte var2);

   Byte2DoubleSortedMap headMap(byte var1);

   Byte2DoubleSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2DoubleSortedMap subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2DoubleSortedMap headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2DoubleSortedMap tailMap(Byte from) {
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
   default ObjectSortedSet<Entry<Byte, Double>> entrySet() {
      return this.byte2DoubleEntrySet();
   }

   ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet();

   ByteSortedSet keySet();

   @Override
   DoubleCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2DoubleMap.Entry>, Byte2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2DoubleMap.Entry> fastIterator(Byte2DoubleMap.Entry var1);
   }
}
