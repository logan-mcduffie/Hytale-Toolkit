package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2CharSortedMap extends Double2CharMap, SortedMap<Double, Character> {
   Double2CharSortedMap subMap(double var1, double var3);

   Double2CharSortedMap headMap(double var1);

   Double2CharSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2CharSortedMap subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2CharSortedMap headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2CharSortedMap tailMap(Double from) {
      return this.tailMap(from.doubleValue());
   }

   @Deprecated
   default Double firstKey() {
      return this.firstDoubleKey();
   }

   @Deprecated
   default Double lastKey() {
      return this.lastDoubleKey();
   }

   @Deprecated
   default ObjectSortedSet<Entry<Double, Character>> entrySet() {
      return this.double2CharEntrySet();
   }

   ObjectSortedSet<Double2CharMap.Entry> double2CharEntrySet();

   DoubleSortedSet keySet();

   @Override
   CharCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2CharMap.Entry>, Double2CharMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2CharMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2CharMap.Entry> fastIterator(Double2CharMap.Entry var1);
   }
}
