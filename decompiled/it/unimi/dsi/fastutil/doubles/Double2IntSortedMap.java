package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2IntSortedMap extends Double2IntMap, SortedMap<Double, Integer> {
   Double2IntSortedMap subMap(double var1, double var3);

   Double2IntSortedMap headMap(double var1);

   Double2IntSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2IntSortedMap subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2IntSortedMap headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2IntSortedMap tailMap(Double from) {
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
   default ObjectSortedSet<Entry<Double, Integer>> entrySet() {
      return this.double2IntEntrySet();
   }

   ObjectSortedSet<Double2IntMap.Entry> double2IntEntrySet();

   DoubleSortedSet keySet();

   @Override
   IntCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2IntMap.Entry>, Double2IntMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2IntMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2IntMap.Entry> fastIterator(Double2IntMap.Entry var1);
   }
}
