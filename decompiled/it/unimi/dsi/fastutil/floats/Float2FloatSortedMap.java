package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Float2FloatSortedMap extends Float2FloatMap, SortedMap<Float, Float> {
   Float2FloatSortedMap subMap(float var1, float var2);

   Float2FloatSortedMap headMap(float var1);

   Float2FloatSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   @Deprecated
   default Float2FloatSortedMap subMap(Float from, Float to) {
      return this.subMap(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default Float2FloatSortedMap headMap(Float to) {
      return this.headMap(to.floatValue());
   }

   @Deprecated
   default Float2FloatSortedMap tailMap(Float from) {
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
   default ObjectSortedSet<Entry<Float, Float>> entrySet() {
      return this.float2FloatEntrySet();
   }

   ObjectSortedSet<Float2FloatMap.Entry> float2FloatEntrySet();

   FloatSortedSet keySet();

   @Override
   FloatCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2FloatMap.Entry>, Float2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2FloatMap.Entry> fastIterator(Float2FloatMap.Entry var1);
   }
}
