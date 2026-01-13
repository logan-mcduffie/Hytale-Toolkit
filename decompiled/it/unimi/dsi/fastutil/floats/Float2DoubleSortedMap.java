package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Float2DoubleSortedMap extends Float2DoubleMap, SortedMap<Float, Double> {
   Float2DoubleSortedMap subMap(float var1, float var2);

   Float2DoubleSortedMap headMap(float var1);

   Float2DoubleSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   @Deprecated
   default Float2DoubleSortedMap subMap(Float from, Float to) {
      return this.subMap(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default Float2DoubleSortedMap headMap(Float to) {
      return this.headMap(to.floatValue());
   }

   @Deprecated
   default Float2DoubleSortedMap tailMap(Float from) {
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
   default ObjectSortedSet<Entry<Float, Double>> entrySet() {
      return this.float2DoubleEntrySet();
   }

   ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet();

   FloatSortedSet keySet();

   @Override
   DoubleCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2DoubleMap.Entry>, Float2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2DoubleMap.Entry> fastIterator(Float2DoubleMap.Entry var1);
   }
}
