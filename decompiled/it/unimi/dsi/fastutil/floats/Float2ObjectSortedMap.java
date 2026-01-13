package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Float2ObjectSortedMap<V> extends Float2ObjectMap<V>, SortedMap<Float, V> {
   Float2ObjectSortedMap<V> subMap(float var1, float var2);

   Float2ObjectSortedMap<V> headMap(float var1);

   Float2ObjectSortedMap<V> tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   @Deprecated
   default Float2ObjectSortedMap<V> subMap(Float from, Float to) {
      return this.subMap(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default Float2ObjectSortedMap<V> headMap(Float to) {
      return this.headMap(to.floatValue());
   }

   @Deprecated
   default Float2ObjectSortedMap<V> tailMap(Float from) {
      return this.tailMap(from.floatValue());
   }

   @Deprecated
   default Float firstKey() {
      return this.firstFloatKey();
   }

   @Deprecated
   default Float lastKey() {
      return this.lastFloatKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Float, V>> entrySet() {
      return this.float2ObjectEntrySet();
   }

   ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet();

   FloatSortedSet keySet();

   @Override
   ObjectCollection<V> values();

   FloatComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Float2ObjectMap.Entry<V>>, Float2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> fastIterator(Float2ObjectMap.Entry<V> var1);
   }
}
