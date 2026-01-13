package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Float2ReferenceSortedMap<V> extends Float2ReferenceMap<V>, SortedMap<Float, V> {
   Float2ReferenceSortedMap<V> subMap(float var1, float var2);

   Float2ReferenceSortedMap<V> headMap(float var1);

   Float2ReferenceSortedMap<V> tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   @Deprecated
   default Float2ReferenceSortedMap<V> subMap(Float from, Float to) {
      return this.subMap(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default Float2ReferenceSortedMap<V> headMap(Float to) {
      return this.headMap(to.floatValue());
   }

   @Deprecated
   default Float2ReferenceSortedMap<V> tailMap(Float from) {
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
      return this.float2ReferenceEntrySet();
   }

   ObjectSortedSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet();

   FloatSortedSet keySet();

   @Override
   ReferenceCollection<V> values();

   FloatComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Float2ReferenceMap.Entry<V>>, Float2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> fastIterator(Float2ReferenceMap.Entry<V> var1);
   }
}
