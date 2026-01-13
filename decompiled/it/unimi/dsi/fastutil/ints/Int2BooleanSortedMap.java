package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Int2BooleanSortedMap extends Int2BooleanMap, SortedMap<Integer, Boolean> {
   Int2BooleanSortedMap subMap(int var1, int var2);

   Int2BooleanSortedMap headMap(int var1);

   Int2BooleanSortedMap tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   @Deprecated
   default Int2BooleanSortedMap subMap(Integer from, Integer to) {
      return this.subMap(from.intValue(), to.intValue());
   }

   @Deprecated
   default Int2BooleanSortedMap headMap(Integer to) {
      return this.headMap(to.intValue());
   }

   @Deprecated
   default Int2BooleanSortedMap tailMap(Integer from) {
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
   default ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
      return this.int2BooleanEntrySet();
   }

   ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet();

   IntSortedSet keySet();

   @Override
   BooleanCollection values();

   IntComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Int2BooleanMap.Entry>, Int2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator(Int2BooleanMap.Entry var1);
   }
}
