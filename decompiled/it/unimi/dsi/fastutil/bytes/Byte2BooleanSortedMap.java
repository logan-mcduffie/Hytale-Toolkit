package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2BooleanSortedMap extends Byte2BooleanMap, SortedMap<Byte, Boolean> {
   Byte2BooleanSortedMap subMap(byte var1, byte var2);

   Byte2BooleanSortedMap headMap(byte var1);

   Byte2BooleanSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2BooleanSortedMap subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2BooleanSortedMap headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2BooleanSortedMap tailMap(Byte from) {
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
   default ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
      return this.byte2BooleanEntrySet();
   }

   ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet();

   ByteSortedSet keySet();

   @Override
   BooleanCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2BooleanMap.Entry>, Byte2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2BooleanMap.Entry> fastIterator(Byte2BooleanMap.Entry var1);
   }
}
