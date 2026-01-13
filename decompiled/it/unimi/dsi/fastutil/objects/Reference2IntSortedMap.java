package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.IntCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Reference2IntSortedMap<K> extends Reference2IntMap<K>, SortedMap<K, Integer> {
   Reference2IntSortedMap<K> subMap(K var1, K var2);

   Reference2IntSortedMap<K> headMap(K var1);

   Reference2IntSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Integer>> entrySet() {
      return this.reference2IntEntrySet();
   }

   ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet();

   ReferenceSortedSet<K> keySet();

   @Override
   IntCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2IntMap.Entry<K>>, Reference2IntMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> fastIterator(Reference2IntMap.Entry<K> var1);
   }
}
