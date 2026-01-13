package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Int2ShortSortedMap extends Int2ShortMap, SortedMap<Integer, Short> {
   Int2ShortSortedMap subMap(int var1, int var2);

   Int2ShortSortedMap headMap(int var1);

   Int2ShortSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   @Deprecated
   default Int2ShortSortedMap subMap(Integer from, Integer to) {
      return this.subMap(from.intValue(), to.intValue());
   }

   @Deprecated
   default Int2ShortSortedMap headMap(Integer to) {
      return this.headMap(to.intValue());
   }

   @Deprecated
   default Int2ShortSortedMap tailMap(Integer from) {
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
   default ObjectSortedSet<Entry<Integer, Short>> entrySet() {
      return this.int2ShortEntrySet();
   }

   ObjectSortedSet<Int2ShortMap.Entry> int2ShortEntrySet();

   IntSortedSet keySet();

   @Override
   ShortCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2ShortMap.Entry>, Int2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2ShortMap.Entry> fastIterator(Int2ShortMap.Entry var1);
   }
}
