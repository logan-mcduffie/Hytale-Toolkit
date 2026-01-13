package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2ReferenceSortedMap<V> extends Byte2ReferenceMap<V>, SortedMap<Byte, V> {
   Byte2ReferenceSortedMap<V> subMap(byte var1, byte var2);

   Byte2ReferenceSortedMap<V> headMap(byte var1);

   Byte2ReferenceSortedMap<V> tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2ReferenceSortedMap<V> subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2ReferenceSortedMap<V> headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2ReferenceSortedMap<V> tailMap(Byte from) {
      return this.tailMap(from.byteValue());
   }

   @Deprecated
   default Byte firstKey() {
      return this.firstByteKey();
   }

   @Deprecated
   default Byte lastKey() {
      return this.lastByteKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Byte, V>> entrySet() {
      return this.byte2ReferenceEntrySet();
   }

   ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet();

   ByteSortedSet keySet();

   @Override
   ReferenceCollection<V> values();

   ByteComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Byte2ReferenceMap.Entry<V>>, Byte2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> fastIterator(Byte2ReferenceMap.Entry<V> var1);
   }
}
