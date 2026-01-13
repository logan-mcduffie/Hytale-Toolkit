package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Int2DoubleSortedMap extends Int2DoubleMap, SortedMap<Integer, Double> {
   Int2DoubleSortedMap subMap(int var1, int var2);

   Int2DoubleSortedMap headMap(int var1);

   Int2DoubleSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   @Deprecated
   default Int2DoubleSortedMap subMap(Integer from, Integer to) {
      return this.subMap(from.intValue(), to.intValue());
   }

   @Deprecated
   default Int2DoubleSortedMap headMap(Integer to) {
      return this.headMap(to.intValue());
   }

   @Deprecated
   default Int2DoubleSortedMap tailMap(Integer from) {
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
   default ObjectSortedSet<Entry<Integer, Double>> entrySet() {
      return this.int2DoubleEntrySet();
   }

   ObjectSortedSet<Int2DoubleMap.Entry> int2DoubleEntrySet();

   IntSortedSet keySet();

   @Override
   DoubleCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2DoubleMap.Entry>, Int2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2DoubleMap.Entry> fastIterator(Int2DoubleMap.Entry var1);
   }
}
