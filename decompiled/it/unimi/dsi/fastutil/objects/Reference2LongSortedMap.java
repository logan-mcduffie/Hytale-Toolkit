package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.LongCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Reference2LongSortedMap<K> extends Reference2LongMap<K>, SortedMap<K, Long> {
   Reference2LongSortedMap<K> subMap(K var1, K var2);

   Reference2LongSortedMap<K> headMap(K var1);

   Reference2LongSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Long>> entrySet() {
      return this.reference2LongEntrySet();
   }

   ObjectSortedSet<Reference2LongMap.Entry<K>> reference2LongEntrySet();

   ReferenceSortedSet<K> keySet();

   @Override
   LongCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2LongMap.Entry<K>>, Reference2LongMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> fastIterator(Reference2LongMap.Entry<K> var1);
   }
}
