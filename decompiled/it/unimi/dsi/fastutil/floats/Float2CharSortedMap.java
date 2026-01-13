package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Float2CharSortedMap extends Float2CharMap, SortedMap<Float, Character> {
   Float2CharSortedMap subMap(float var1, float var2);

   Float2CharSortedMap headMap(float var1);

   Float2CharSortedMap tailMap(float var1);

   float firstFloatKey();

   float lastFloatKey();

   @Deprecated
   default Float2CharSortedMap subMap(Float from, Float to) {
      return this.subMap(from.floatValue(), to.floatValue());
   }

   @Deprecated
   default Float2CharSortedMap headMap(Float to) {
      return this.headMap(to.floatValue());
   }

   @Deprecated
   default Float2CharSortedMap tailMap(Float from) {
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
   default ObjectSortedSet<Entry<Float, Character>> entrySet() {
      return this.float2CharEntrySet();
   }

   ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet();

   FloatSortedSet keySet();

   @Override
   CharCollection values();

   FloatComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Float2CharMap.Entry>, Float2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Float2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Float2CharMap.Entry> fastIterator(Float2CharMap.Entry var1);
   }
}
