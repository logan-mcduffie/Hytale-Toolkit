package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2ObjectSortedMap<V> extends Byte2ObjectMap<V>, SortedMap<Byte, V> {
   Byte2ObjectSortedMap<V> subMap(byte var1, byte var2);

   Byte2ObjectSortedMap<V> headMap(byte var1);

   Byte2ObjectSortedMap<V> tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2ObjectSortedMap<V> subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2ObjectSortedMap<V> headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2ObjectSortedMap<V> tailMap(Byte from) {
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
      return this.byte2ObjectEntrySet();
   }

   ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet();

   ByteSortedSet keySet();

   @Override
   ObjectCollection<V> values();

   ByteComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Byte2ObjectMap.Entry<V>>, Byte2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> fastIterator(Byte2ObjectMap.Entry<V> var1);
   }
}
