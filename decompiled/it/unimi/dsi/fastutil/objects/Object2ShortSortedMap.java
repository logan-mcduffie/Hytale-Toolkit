package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Object2ShortSortedMap<K> extends Object2ShortMap<K>, SortedMap<K, Short> {
   Object2ShortSortedMap<K> subMap(K var1, K var2);

   Object2ShortSortedMap<K> headMap(K var1);

   Object2ShortSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Short>> entrySet() {
      return this.object2ShortEntrySet();
   }

   ObjectSortedSet<Object2ShortMap.Entry<K>> object2ShortEntrySet();

   ObjectSortedSet<K> keySet();

   @Override
   ShortCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Object2ShortMap.Entry<K>>, Object2ShortMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> fastIterator(Object2ShortMap.Entry<K> var1);
   }
}
