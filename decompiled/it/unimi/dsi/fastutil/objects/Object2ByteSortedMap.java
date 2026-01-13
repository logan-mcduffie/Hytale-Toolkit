package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Object2ByteSortedMap<K> extends Object2ByteMap<K>, SortedMap<K, Byte> {
   Object2ByteSortedMap<K> subMap(K var1, K var2);

   Object2ByteSortedMap<K> headMap(K var1);

   Object2ByteSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Byte>> entrySet() {
      return this.object2ByteEntrySet();
   }

   ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet();

   ObjectSortedSet<K> keySet();

   @Override
   ByteCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Object2ByteMap.Entry<K>>, Object2ByteMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteMap.Entry<K> var1);
   }
}
