package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Int2ReferenceSortedMap<V> extends Int2ReferenceMap<V>, SortedMap<Integer, V> {
   Int2ReferenceSortedMap<V> subMap(int var1, int var2);

   Int2ReferenceSortedMap<V> headMap(int var1);

   Int2ReferenceSortedMap<V> tailMap(int var1);

   int firstIntKey();

   int lastIntKey();

   @Deprecated
   default Int2ReferenceSortedMap<V> subMap(Integer from, Integer to) {
      return this.subMap(from.intValue(), to.intValue());
   }

   @Deprecated
   default Int2ReferenceSortedMap<V> headMap(Integer to) {
      return this.headMap(to.intValue());
   }

   @Deprecated
   default Int2ReferenceSortedMap<V> tailMap(Integer from) {
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
   default ObjectSortedSet<Entry<Integer, V>> entrySet() {
      return this.int2ReferenceEntrySet();
   }

   ObjectSortedSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet();

   IntSortedSet keySet();

   @Override
   ReferenceCollection<V> values();

   IntComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Int2ReferenceMap.Entry<V>>, Int2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> fastIterator(Int2ReferenceMap.Entry<V> var1);
   }
}
