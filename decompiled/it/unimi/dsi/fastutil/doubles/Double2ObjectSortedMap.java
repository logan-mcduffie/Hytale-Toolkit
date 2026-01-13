package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;
import java.util.Map.Entry;

public interface Double2ObjectSortedMap<V> extends Double2ObjectMap<V>, SortedMap<Double, V> {
   Double2ObjectSortedMap<V> subMap(double var1, double var3);

   Double2ObjectSortedMap<V> headMap(double var1);

   Double2ObjectSortedMap<V> tailMap(double var1);

   double firstDoubleKey();

   double lastDoubleKey();

   @Deprecated
   default Double2ObjectSortedMap<V> subMap(Double from, Double to) {
      return this.subMap(from.doubleValue(), to.doubleValue());
   }

   @Deprecated
   default Double2ObjectSortedMap<V> headMap(Double to) {
      return this.headMap(to.doubleValue());
   }

   @Deprecated
   default Double2ObjectSortedMap<V> tailMap(Double from) {
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
      return this.double2ObjectEntrySet();
   }

   ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet();

   DoubleSortedSet keySet();

   @Override
   ObjectCollection<V> values();

   DoubleComparator comparator();

   public interface FastSortedEntrySet<V> extends ObjectSortedSet<Double2ObjectMap.Entry<V>>, Double2ObjectMap.FastEntrySet<V> {
      ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> fastIterator();

      ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> fastIterator(Double2ObjectMap.Entry<V> var1);
   }
}
