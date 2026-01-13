package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2ReferenceSortedMap<V> extends Char2ReferenceMap<V>, SortedMap<Character, V> {
   Char2ReferenceSortedMap<V> subMap(char var1, char var2);

   Char2ReferenceSortedMap<V> headMap(char var1);

   Char2ReferenceSortedMap<V> tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2ReferenceSortedMap<V> subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2ReferenceSortedMap<V> headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2ReferenceSortedMap<V> tailMap(Character from) {
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
   default ObjectSortedSet<Entry<Character, V>> entrySet() {
      return this.char2ReferenceEntrySet();
   }

   ObjectSortedSet<Char2ReferenceMap.Entry<V>> char2ReferenceEntrySet();

   CharSortedSet keySet();

   @Override
   ReferenceCollection<V> values();

   CharComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Char2ReferenceMap.Entry<V>>, Char2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> fastIterator(Char2ReferenceMap.Entry<V> var1);
   }
}
