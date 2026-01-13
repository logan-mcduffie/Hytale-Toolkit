package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Reference2ByteSortedMap<K> extends Reference2ByteMap<K>, SortedMap<K, Byte> {
   Reference2ByteSortedMap<K> subMap(K var1, K var2);

   Reference2ByteSortedMap<K> headMap(K var1);

   Reference2ByteSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Byte>> entrySet() {
      return this.reference2ByteEntrySet();
   }

   ObjectSortedSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet();

   ReferenceSortedSet<K> keySet();

   @Override
   ByteCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Reference2ByteMap.Entry<K>>, Reference2ByteMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> fastIterator(Reference2ByteMap.Entry<K> var1);
   }
}
