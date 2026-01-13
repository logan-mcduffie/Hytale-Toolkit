package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2FloatSortedMap extends Double2FloatMap, SortedMap<Double, Float> {
   Double2FloatSortedMap subMap(double var1, double var3);

   Double2FloatSortedMap headMap(double var1);

   Double2FloatSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2FloatSortedMap subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2FloatSortedMap headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2FloatSortedMap tailMap(Double from) {
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
   default ObjectSortedSet<Entry<Double, Float>> entrySet() {
      return this.double2FloatEntrySet();
   }

   ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet();

   DoubleSortedSet keySet();

   @Override
   FloatCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2FloatMap.Entry>, Double2FloatMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2FloatMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2FloatMap.Entry> fastIterator(Double2FloatMap.Entry var1);
   }
}
