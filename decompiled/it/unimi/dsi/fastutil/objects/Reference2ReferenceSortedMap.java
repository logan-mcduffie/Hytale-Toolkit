package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Reference2ReferenceSortedMap<K, V> extends Reference2ReferenceMap<K, V>, SortedMap<K, V> {
   Reference2ReferenceSortedMap<K, V> subMap(K var1, K var2);

   Reference2ReferenceSortedMap<K, V> headMap(K var1);

   Reference2ReferenceSortedMap<K, V> tailMap(K var1);

   default ObjectSortedSet<Entry<K, V>> entrySet() {
      return this.reference2ReferenceEntrySet();
   }

   ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet();

   ReferenceSortedSet<K> keySet();

   @Override
   ReferenceCollection<V> values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K, V> extends ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>>, Reference2ReferenceMap.FastEntrySet<K, V> {
      ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator();

      ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator(Reference2ReferenceMap.Entry<K, V> var1);
   }
}
