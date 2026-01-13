package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.CharCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Reference2CharSortedMap<K> extends Reference2CharMap<K>, SortedMap<K, Character> {
   Reference2CharSortedMap<K> subMap(K var1, K var2);

   Reference2CharSortedMap<K> headMap(K var1);

   Reference2CharSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Character>> entrySet() {
      return this.reference2CharEntrySet();
   }

   ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet();

   ReferenceSortedSet<K> keySet();

   @Override
   CharCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2CharMap.Entry<K>>, Reference2CharMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> fastIterator(Reference2CharMap.Entry<K> var1);
   }
}
