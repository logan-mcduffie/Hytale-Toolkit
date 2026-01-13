package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Reference2FloatSortedMap<K> extends Reference2FloatMap<K>, SortedMap<K, Float> {
   Reference2FloatSortedMap<K> subMap(K var1, K var2);

   Reference2FloatSortedMap<K> headMap(K var1);

   Reference2FloatSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Float>> entrySet() {
      return this.reference2FloatEntrySet();
   }

   ObjectSortedSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet();

   ReferenceSortedSet<K> keySet();

   @Override
   FloatCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2FloatMap.Entry<K>>, Reference2FloatMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> fastIterator(Reference2FloatMap.Entry<K> var1);
   }
}
