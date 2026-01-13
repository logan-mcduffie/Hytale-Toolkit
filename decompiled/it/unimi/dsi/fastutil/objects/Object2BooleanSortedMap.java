package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Object2BooleanSortedMap<K> extends Object2BooleanMap<K>, SortedMap<K, Boolean> {
   Object2BooleanSortedMap<K> subMap(K var1, K var2);

   Object2BooleanSortedMap<K> headMap(K var1);

   Object2BooleanSortedMap<K> tailMap(K var1);

   @Deprecated
   default ObjectSortedSet<Entry<K, Boolean>> entrySet() {
      return this.object2BooleanEntrySet();
   }

   ObjectSortedSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet();

   ObjectSortedSet<K> keySet();

   @Override
   BooleanCollection values();

   @Override
   Comparator<? super K> comparator();

   public interface FastSortedEntrySet<K> extends ObjectSortedSet<Object2BooleanMap.Entry<K>>, Object2BooleanMap.FastEntrySet<K> {
      ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> fastIterator();

      ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> fastIterator(Object2BooleanMap.Entry<K> var1);
   }
}
