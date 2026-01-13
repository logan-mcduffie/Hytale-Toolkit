package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2DoubleSortedMap extends Char2DoubleMap, SortedMap<Character, Double> {
   Char2DoubleSortedMap subMap(char var1, char var2);

   Char2DoubleSortedMap headMap(char var1);

   Char2DoubleSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2DoubleSortedMap subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2DoubleSortedMap headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2DoubleSortedMap tailMap(Character from) {
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
   default ObjectSortedSet<Entry<Character, Double>> entrySet() {
      return this.char2DoubleEntrySet();
   }

   ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet();

   CharSortedSet keySet();

   @Override
   DoubleCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2DoubleMap.Entry>, Char2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2DoubleMap.Entry> fastIterator(Char2DoubleMap.Entry var1);
   }
}
