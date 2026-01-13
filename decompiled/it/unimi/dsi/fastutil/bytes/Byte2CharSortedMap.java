package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Byte2CharSortedMap extends Byte2CharMap, SortedMap<Byte, Character> {
   Byte2CharSortedMap subMap(byte var1, byte var2);

   Byte2CharSortedMap headMap(byte var1);

   Byte2CharSortedMap tailMap(byte var1);

   byte firstByteKey();

   byte lastByteKey();

   @Deprecated
   default Byte2CharSortedMap subMap(Byte from, Byte to) {
      return this.subMap(from.byteValue(), to.byteValue());
   }

   @Deprecated
   default Byte2CharSortedMap headMap(Byte to) {
      return this.headMap(to.byteValue());
   }

   @Deprecated
   default Byte2CharSortedMap tailMap(Byte from) {
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
   default ObjectSortedSet<Entry<Byte, Character>> entrySet() {
      return this.byte2CharEntrySet();
   }

   ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet();

   ByteSortedSet keySet();

   @Override
   CharCollection values();

   ByteComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Byte2CharMap.Entry>, Byte2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Byte2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Byte2CharMap.Entry> fastIterator(Byte2CharMap.Entry var1);
   }
}
