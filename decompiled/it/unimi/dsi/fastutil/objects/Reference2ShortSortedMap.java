package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Reference2ShortSortedMap<K> extends Reference2ShortMap<K>, SortedMap<K, Short> {
   Reference2ShortSortedMap<K> subMap(K var1, K var2);

   Reference2ShortSortedMap<K> headMap(K var1);

   Reference2ShortSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Short>> entrySet() {
      return this.reference2ShortEntrySet();
   }

   ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet();

   ReferenceSortedSet<K> keySet();

   @Override
   ShortCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2ShortMap.Entry<K>>, Reference2ShortMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> fastIterator(Reference2ShortMap.Entry<K> var1);
   }
}
