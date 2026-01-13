package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2ObjectSortedMap<V> extends Short2ObjectMap<V>, SortedMap<Short, V> {
   Short2ObjectSortedMap<V> subMap(short var1, short var2);

   Short2ObjectSortedMap<V> headMap(short var1);

   Short2ObjectSortedMap<V> tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2ObjectSortedMap<V> subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2ObjectSortedMap<V> headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2ObjectSortedMap<V> tailMap(Short from) {
      return this.tailMap(from.shortValue());
   }

   @Deprecated
   default Short firstKey() {
      return this.firstShortKey();
   }

   @Deprecated
   default Short lastKey() {
      return this.lastShortKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Short, V>> entrySet() {
      return this.short2ObjectEntrySet();
   }

   ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet();

   ShortSortedSet keySet();

   @Override
   ObjectCollection<V> values();

   ShortComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Short2ObjectMap.Entry<V>>, Short2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> fastIterator(Short2ObjectMap.Entry<V> var1);
   }
}
