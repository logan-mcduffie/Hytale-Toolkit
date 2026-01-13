package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Float2IntSortedMap extends Float2IntMap, SortedMap<Float, Integer> {
   Float2IntSortedMap subMap(float var1, float var2);

   Float2IntSortedMap headMap(float var1);

   Float2IntSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   @Deprecated
   default Float2IntSortedMap subMap(Float from, Float to) {
      return this.subMap(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default Float2IntSortedMap headMap(Float to) {
      return this.headMap(to.floatValue());
   }

   @Deprecated
   default Float2IntSortedMap tailMap(Float from) {
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
   default ObjectSortedSet<Entry<Float, Integer>> entrySet() {
      return this.float2IntEntrySet();
   }

   ObjectSortedSet<Float2IntMap.Entry> float2IntEntrySet();

   FloatSortedSet keySet();

   @Override
   IntCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2IntMap.Entry>, Float2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2IntMap.Entry> fastIterator(Float2IntMap.Entry var1);
   }
}
