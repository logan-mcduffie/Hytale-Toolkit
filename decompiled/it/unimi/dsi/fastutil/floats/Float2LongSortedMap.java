package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Float2LongSortedMap extends Float2LongMap, SortedMap<Float, Long> {
   Float2LongSortedMap subMap(float var1, float var2);

   Float2LongSortedMap headMap(float var1);

   Float2LongSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   @Deprecated
   default Float2LongSortedMap subMap(Float from, Float to) {
      return this.subMap(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default Float2LongSortedMap headMap(Float to) {
      return this.headMap(to.floatValue());
   }

   @Deprecated
   default Float2LongSortedMap tailMap(Float from) {
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
   default ObjectSortedSet<Entry<Float, Long>> entrySet() {
      return this.float2LongEntrySet();
   }

   ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet();

   FloatSortedSet keySet();

   @Override
   LongCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2LongMap.Entry>, Float2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2LongMap.Entry> fastIterator(Float2LongMap.Entry var1);
   }
}
