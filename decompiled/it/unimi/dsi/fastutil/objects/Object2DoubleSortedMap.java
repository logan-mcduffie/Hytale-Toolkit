package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Object2DoubleSortedMap<K> extends Object2DoubleMap<K>, SortedMap<K, Double> {
   Object2DoubleSortedMap<K> subMap(K var1, K var2);

   Object2DoubleSortedMap<K> headMap(K var1);

   Object2DoubleSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Double>> entrySet() {
      return this.object2DoubleEntrySet();
   }

   ObjectSortedSet<Object2DoubleMap.Entry<K>> object2DoubleEntrySet();

   ObjectSortedSet<K> keySet();

   @Override
   DoubleCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Object2DoubleMap.Entry<K>>, Object2DoubleMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> fastIterator(Object2DoubleMap.Entry<K> var1);
   }
}
