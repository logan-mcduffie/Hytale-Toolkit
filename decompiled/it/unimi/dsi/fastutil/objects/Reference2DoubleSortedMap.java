package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Reference2DoubleSortedMap<K> extends Reference2DoubleMap<K>, SortedMap<K, Double> {
   Reference2DoubleSortedMap<K> subMap(K var1, K var2);

   Reference2DoubleSortedMap<K> headMap(K var1);

   Reference2DoubleSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Double>> entrySet() {
      return this.reference2DoubleEntrySet();
   }

   ObjectSortedSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet();

   ReferenceSortedSet<K> keySet();

   @Override
   DoubleCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2DoubleMap.Entry<K>>, Reference2DoubleMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> fastIterator(Reference2DoubleMap.Entry<K> var1);
   }
}
