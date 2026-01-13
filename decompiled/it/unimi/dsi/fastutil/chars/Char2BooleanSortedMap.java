package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2BooleanSortedMap extends Char2BooleanMap, SortedMap<Character, Boolean> {
   Char2BooleanSortedMap subMap(char var1, char var2);

   Char2BooleanSortedMap headMap(char var1);

   Char2BooleanSortedMap tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2BooleanSortedMap subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2BooleanSortedMap headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2BooleanSortedMap tailMap(Character from) {
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
   default ObjectSortedSet<Entry<Character, Boolean>> entrySet() {
      return this.char2BooleanEntrySet();
   }

   ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet();

   CharSortedSet keySet();

   @Override
   BooleanCollection values();

   CharComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Char2BooleanMap.Entry>, Char2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Char2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Char2BooleanMap.Entry> fastIterator(Char2BooleanMap.Entry var1);
   }
}
