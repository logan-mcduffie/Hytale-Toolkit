package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Int2ByteSortedMap extends Int2ByteMap, SortedMap<Integer, Byte> {
   Int2ByteSortedMap subMap(int var1, int var2);

   Int2ByteSortedMap headMap(int var1);

   Int2ByteSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   @Deprecated
   default Int2ByteSortedMap subMap(Integer from, Integer to) {
      return this.subMap(from.intValue(), to.intValue());
   }

   @Deprecated
   default Int2ByteSortedMap headMap(Integer to) {
      return this.headMap(to.intValue());
   }

   @Deprecated
   default Int2ByteSortedMap tailMap(Integer from) {
      return this.tailMap(from.intValue());
   }

   @Deprecated
   default Integer firstKey() {
      return this.firstIntKey();
   }

   @Deprecated
   default Integer lastKey() {
      return this.lastIntKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Integer, Byte>> entrySet() {
      return this.int2ByteEntrySet();
   }

   ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet();

   IntSortedSet keySet();

   @Override
   ByteCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2ByteMap.Entry>, Int2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2ByteMap.Entry> fastIterator(Int2ByteMap.Entry var1);
   }
}
