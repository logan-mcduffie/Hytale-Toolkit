package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2ByteSortedMap extends Char2ByteMap, SortedMap<Character, Byte> {
   Char2ByteSortedMap subMap(char var1, char var2);

   Char2ByteSortedMap headMap(char var1);

   Char2ByteSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2ByteSortedMap subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2ByteSortedMap headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2ByteSortedMap tailMap(Character from) {
      return this.tailMap(from.charValue());
   }

   @Deprecated
   default Character firstKey() {
      return this.firstCharKey();
   }

   @Deprecated
   default Character lastKey() {
      return this.lastCharKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Character, Byte>> entrySet() {
      return this.char2ByteEntrySet();
   }

   ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet();

   CharSortedSet keySet();

   @Override
   ByteCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2ByteMap.Entry>, Char2ByteMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator(Char2ByteMap.Entry var1);
   }
}
