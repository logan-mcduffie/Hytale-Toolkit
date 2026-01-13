package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Char2ObjectSortedMap<V> extends Char2ObjectMap<V>, SortedMap<Character, V> {
   Char2ObjectSortedMap<V> subMap(char var1, char var2);

   Char2ObjectSortedMap<V> headMap(char var1);

   Char2ObjectSortedMap<V> tailMap(char var1);

   char firstCharKey();

   char lastCharKey();

   @Deprecated
   default Char2ObjectSortedMap<V> subMap(Character from, Character to) {
      return this.subMap(from.charValue(), to.charValue());
   }

   @Deprecated
   default Char2ObjectSortedMap<V> headMap(Character to) {
      return this.headMap(to.charValue());
   }

   @Deprecated
   default Char2ObjectSortedMap<V> tailMap(Character from) {
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
      return this.char2ObjectEntrySet();
   }

   ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet();

   CharSortedSet keySet();

   @Override
   ObjectCollection<V> values();

   CharComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Char2ObjectMap.Entry<V>>, Char2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> fastIterator(Char2ObjectMap.Entry<V> var1);
   }
}
