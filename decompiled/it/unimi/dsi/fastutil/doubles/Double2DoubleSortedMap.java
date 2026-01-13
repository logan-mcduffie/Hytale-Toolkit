package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2DoubleSortedMap extends Double2DoubleMap, SortedMap<Double, Double> {
   Double2DoubleSortedMap subMap(double var1, double var3);

   Double2DoubleSortedMap headMap(double var1);

   Double2DoubleSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2DoubleSortedMap subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2DoubleSortedMap headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2DoubleSortedMap tailMap(Double from) {
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
   default ObjectSortedSet<Entry<Double, Double>> entrySet() {
      return this.double2DoubleEntrySet();
   }

   ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet();

   DoubleSortedSet keySet();

   @Override
   DoubleCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2DoubleMap.Entry>, Double2DoubleMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2DoubleMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2DoubleMap.Entry> fastIterator(Double2DoubleMap.Entry var1);
   }
}
