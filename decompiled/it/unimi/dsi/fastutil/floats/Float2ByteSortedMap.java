package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Float2ByteSortedMap extends Float2ByteMap, SortedMap<Float, Byte> {
   Float2ByteSortedMap subMap(float var1, float var2);

   Float2ByteSortedMap headMap(float var1);

   Float2ByteSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   @Deprecated
   default Float2ByteSortedMap subMap(Float from, Float to) {
      return this.subMap(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default Float2ByteSortedMap headMap(Float to) {
      return this.headMap(to.floatValue());
   }

   @Deprecated
   default Float2ByteSortedMap tailMap(Float from) {
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
   default ObjectSortedSet<Entry<Float, Byte>> entrySet() {
      return this.float2ByteEntrySet();
   }

   ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet();

   FloatSortedSet keySet();

   @Override
   ByteCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2ByteMap.Entry>, Float2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2ByteMap.Entry> fastIterator(Float2ByteMap.Entry var1);
   }
}
