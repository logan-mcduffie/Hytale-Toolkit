package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Object2ReferenceSortedMap<K, V> extends Object2ReferenceMap<K, V>, SortedMap<K, V> {
   Object2ReferenceSortedMap<K, V> subMap(K var1, K var2);

   Object2ReferenceSortedMap<K, V> headMap(K var1);

   Object2ReferenceSortedMap<K, V> tailMap(K var1);

   default ObjectSortedSet<Entry<K, V>> entrySet() {
      return this.object2ReferenceEntrySet();
   }

   ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet();

   ObjectSortedSet<K> keySet();

   @Override
   ReferenceCollection<V> values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K, V> extends ObjectSortedSet<Object2ReferenceMap.Entry<K, V>>, Object2ReferenceMap.FastEntrySet<K, V> {
      ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> fastIterator();

      ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> fastIterator(Object2ReferenceMap.Entry<K, V> var1);
   }
}
