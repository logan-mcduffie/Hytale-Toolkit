package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2LongSortedMap extends Double2LongMap, SortedMap<Double, Long> {
   Double2LongSortedMap subMap(double var1, double var3);

   Double2LongSortedMap headMap(double var1);

   Double2LongSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2LongSortedMap subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2LongSortedMap headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2LongSortedMap tailMap(Double from) {
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
   default ObjectSortedSet<Entry<Double, Long>> entrySet() {
      return this.double2LongEntrySet();
   }

   ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet();

   DoubleSortedSet keySet();

   @Override
   LongCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2LongMap.Entry>, Double2LongMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2LongMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2LongMap.Entry> fastIterator(Double2LongMap.Entry var1);
   }
}
