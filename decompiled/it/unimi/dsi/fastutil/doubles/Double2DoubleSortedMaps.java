package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Double2DoubleSortedMaps {
   public static final Double2DoubleSortedMaps.EmptySortedMap EMPTY_MAP = new Double2DoubleSortedMaps.EmptySortedMap();

   private Double2DoubleSortedMaps() {
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().doubleValue(), y.getKey().doubleValue());
   }

   public static ObjectBidirectionalIterator<Double2DoubleMap.Entry> fastIterator(Double2DoubleSortedMap map) {
      ObjectSortedSet<Double2DoubleMap.Entry> entries = map.double2DoubleEntrySet();
      return entries instanceof Double2DoubleSortedMap.FastSortedEntrySet
         ? ((Double2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Double2DoubleMap.Entry> fastIterable(Double2DoubleSortedMap map) {
      ObjectSortedSet<Double2DoubleMap.Entry> entries = map.double2DoubleEntrySet();
      return (ObjectBidirectionalIterable<Double2DoubleMap.Entry>)(entries instanceof Double2DoubleSortedMap.FastSortedEntrySet
         ? ((Double2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Double2DoubleSortedMap singleton(Double key, Double value) {
      return new Double2DoubleSortedMaps.Singleton(key, value);
   }

   public static Double2DoubleSortedMap singleton(Double key, Double value, DoubleComparator comparator) {
      return new Double2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2DoubleSortedMap singleton(double key, double value) {
      return new Double2DoubleSortedMaps.Singleton(key, value);
   }

   public static Double2DoubleSortedMap singleton(double key, double value, DoubleComparator comparator) {
      return new Double2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2DoubleSortedMap synchronize(Double2DoubleSortedMap m) {
      return new Double2DoubleSortedMaps.SynchronizedSortedMap(m);
   }

   public static Double2DoubleSortedMap synchronize(Double2DoubleSortedMap m, Object sync) {
      return new Double2DoubleSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Double2DoubleSortedMap unmodifiable(Double2DoubleSortedMap m) {
      return new Double2DoubleSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Double2DoubleMaps.EmptyMap implements Double2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Double>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet keySet() {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public Double2DoubleSortedMap subMap(double from, double to) {
         return Double2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2DoubleSortedMap headMap(double to) {
         return Double2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2DoubleSortedMap tailMap(double from) {
         return Double2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public double firstDoubleKey() {
         throw new NoSuchElementException();
      }

      @Override
      public double lastDoubleKey() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap subMap(Double ofrom, Double oto) {
         return this.subMap(ofrom.doubleValue(), oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double firstKey() {
         return this.firstDoubleKey();
      }

      @Deprecated
      @Override
      public Double lastKey() {
         return this.lastDoubleKey();
      }
   }

   public static class Singleton extends Double2DoubleMaps.Singleton implements Double2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleComparator comparator;

      protected Singleton(double key, double value, DoubleComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(double key, double value) {
         this(key, value, null);
      }

      final int compare(double k1, double k2) {
         return this.comparator == null ? Double.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public DoubleComparator comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractDouble2DoubleMap.BasicEntry(this.key, this.value), Double2DoubleSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Double2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2DoubleSortedMap subMap(double from, double to) {
         return (Double2DoubleSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Double2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2DoubleSortedMap headMap(double to) {
         return (Double2DoubleSortedMap)(this.compare(this.key, to) < 0 ? this : Double2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2DoubleSortedMap tailMap(double from) {
         return (Double2DoubleSortedMap)(this.compare(from, this.key) <= 0 ? this : Double2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public double firstDoubleKey() {
         return this.key;
      }

      @Override
      public double lastDoubleKey() {
         return this.key;
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap subMap(Double ofrom, Double oto) {
         return this.subMap(ofrom.doubleValue(), oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double firstKey() {
         return this.firstDoubleKey();
      }

      @Deprecated
      @Override
      public Double lastKey() {
         return this.lastDoubleKey();
      }
   }

   public static class SynchronizedSortedMap extends Double2DoubleMaps.SynchronizedMap implements Double2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2DoubleSortedMap sortedMap;

      protected SynchronizedSortedMap(Double2DoubleSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Double2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         synchronized (this.sync) {
            return this.sortedMap.comparator();
         }
      }

      @Override
      public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2DoubleEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Double2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2DoubleSortedMap subMap(double from, double to) {
         return new Double2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Double2DoubleSortedMap headMap(double to) {
         return new Double2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Double2DoubleSortedMap tailMap(double from) {
         return new Double2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }

      @Override
      public double firstDoubleKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstDoubleKey();
         }
      }

      @Override
      public double lastDoubleKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastDoubleKey();
         }
      }

      @Deprecated
      @Override
      public Double firstKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      @Deprecated
      @Override
      public Double lastKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap subMap(Double from, Double to) {
         return new Double2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap headMap(Double to) {
         return new Double2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap tailMap(Double from) {
         return new Double2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Double2DoubleMaps.UnmodifiableMap implements Double2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2DoubleSortedMap sortedMap;

      protected UnmodifiableSortedMap(Double2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2DoubleEntrySet());
         }

         return (ObjectSortedSet<Double2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2DoubleSortedMap subMap(double from, double to) {
         return new Double2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Double2DoubleSortedMap headMap(double to) {
         return new Double2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Double2DoubleSortedMap tailMap(double from) {
         return new Double2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }

      @Override
      public double firstDoubleKey() {
         return this.sortedMap.firstDoubleKey();
      }

      @Override
      public double lastDoubleKey() {
         return this.sortedMap.lastDoubleKey();
      }

      @Deprecated
      @Override
      public Double firstKey() {
         return this.sortedMap.firstKey();
      }

      @Deprecated
      @Override
      public Double lastKey() {
         return this.sortedMap.lastKey();
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap subMap(Double from, Double to) {
         return new Double2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap headMap(Double to) {
         return new Double2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Double2DoubleSortedMap tailMap(Double from) {
         return new Double2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
