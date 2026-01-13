package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2ShortSortedMap extends Double2ShortMap, SortedMap<Double, Short> {
   Double2ShortSortedMap subMap(double var1, double var3);

   Double2ShortSortedMap headMap(double var1);

   Double2ShortSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2ShortSortedMap subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2ShortSortedMap headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2ShortSortedMap tailMap(Double from) {
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
   default ObjectSortedSet<Entry<Double, Short>> entrySet() {
      return this.double2ShortEntrySet();
   }

   ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet();

   DoubleSortedSet keySet();

   @Override
   ShortCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2ShortMap.Entry>, Double2ShortMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2ShortMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2ShortMap.Entry> fastIterator(Double2ShortMap.Entry var1);
   }
}
