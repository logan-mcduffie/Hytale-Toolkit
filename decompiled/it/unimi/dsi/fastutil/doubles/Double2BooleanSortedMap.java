package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2BooleanSortedMap extends Double2BooleanMap, SortedMap<Double, Boolean> {
   Double2BooleanSortedMap subMap(double var1, double var3);

   Double2BooleanSortedMap headMap(double var1);

   Double2BooleanSortedMap tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2BooleanSortedMap subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2BooleanSortedMap headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2BooleanSortedMap tailMap(Double from) {
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
   default ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
      return this.double2BooleanEntrySet();
   }

   ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet();

   DoubleSortedSet keySet();

   @Override
   BooleanCollection values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet extends ObjectSortedSet<Double2BooleanMap.Entry>, Double2BooleanMap.FastEntrySet {
      ObjectBidirectionalIterator<Double2BooleanMap.Entry> fastIterator();

      ObjectBidirectionalIterator<Double2BooleanMap.Entry> fastIterator(Double2BooleanMap.Entry var1);
   }
}
