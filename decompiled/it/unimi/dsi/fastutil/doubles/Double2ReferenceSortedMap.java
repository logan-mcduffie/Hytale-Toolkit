package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2ReferenceSortedMap<V> extends Double2ReferenceMap<V>, SortedMap<Double, V> {
   Double2ReferenceSortedMap<V> subMap(double var1, double var3);

   Double2ReferenceSortedMap<V> headMap(double var1);

   Double2ReferenceSortedMap<V> tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2ReferenceSortedMap<V> subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2ReferenceSortedMap<V> headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2ReferenceSortedMap<V> tailMap(Double from) {
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
   default ObjectSortedSet<Entry<Double, V>> entrySet() {
      return this.double2ReferenceEntrySet();
   }

   ObjectSortedSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet();

   DoubleSortedSet keySet();

   @Override
   ReferenceCollection<V> values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Double2ReferenceMap.Entry<V>>, Double2ReferenceMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> fastIterator(Double2ReferenceMap.Entry<V> var1);
   }
}
