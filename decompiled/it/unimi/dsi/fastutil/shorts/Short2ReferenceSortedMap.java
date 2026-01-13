package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Short2ReferenceSortedMap<V> extends Short2ReferenceMap<V>, SortedMap<Short, V> {
   Short2ReferenceSortedMap<V> subMap(short var1, short var2);

   Short2ReferenceSortedMap<V> headMap(short var1);

   Short2ReferenceSortedMap<V> tailMap(short var1);

   short firstShortKey();

   short lastShortKey();

   @Deprecated
   default Short2ReferenceSortedMap<V> subMap(Short from, Short to) {
      return this.subMap(from.shortValue(), to.shortValue());
   }

   @Deprecated
   default Short2ReferenceSortedMap<V> headMap(Short to) {
      return this.headMap(to.shortValue());
   }

   @Deprecated
   default Short2ReferenceSortedMap<V> tailMap(Short from) {
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
      return this.short2ReferenceEntrySet();
   }

   ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet();

   ShortSortedSet keySet();

   @Override
   ReferenceCollection<V> values();

   ShortComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Short2ReferenceMap.Entry<V>>, Short2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> fastIterator(Short2ReferenceMap.Entry<V> var1);
   }
}
