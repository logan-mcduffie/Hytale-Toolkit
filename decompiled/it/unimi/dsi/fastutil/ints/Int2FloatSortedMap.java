package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Int2FloatSortedMap extends Int2FloatMap, SortedMap<Integer, Float> {
   Int2FloatSortedMap subMap(int var1, int var2);

   Int2FloatSortedMap headMap(int var1);

   Int2FloatSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   @Deprecated
   default Int2FloatSortedMap subMap(Integer from, Integer to) {
      return this.subMap(from.intValue(), to.intValue());
   }

   @Deprecated
   default Int2FloatSortedMap headMap(Integer to) {
      return this.headMap(to.intValue());
   }

   @Deprecated
   default Int2FloatSortedMap tailMap(Integer from) {
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
   default ObjectSortedSet<Entry<Integer, Float>> entrySet() {
      return this.int2FloatEntrySet();
   }

   ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet();

   IntSortedSet keySet();

   @Override
   FloatCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2FloatMap.Entry>, Int2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator(Int2FloatMap.Entry var1);
   }
}
