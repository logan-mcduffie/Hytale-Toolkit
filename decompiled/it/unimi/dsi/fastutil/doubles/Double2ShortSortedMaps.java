package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Double2ShortSortedMaps {
   public static final Double2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Double2ShortSortedMaps.EmptySortedMap();

   private Double2ShortSortedMaps() {
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().doubleValue(), y.getKey().doubleValue());
   }

   public static ObjectBidirectionalIterator<Double2ShortMap.Entry> fastIterator(Double2ShortSortedMap map) {
      ObjectSortedSet<Double2ShortMap.Entry> entries = map.double2ShortEntrySet();
      return entries instanceof Double2ShortSortedMap.FastSortedEntrySet
         ? ((Double2ShortSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Double2ShortMap.Entry> fastIterable(Double2ShortSortedMap map) {
      ObjectSortedSet<Double2ShortMap.Entry> entries = map.double2ShortEntrySet();
      return (ObjectBidirectionalIterable<Double2ShortMap.Entry>)(entries instanceof Double2ShortSortedMap.FastSortedEntrySet
         ? ((Double2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Double2ShortSortedMap singleton(Double key, Short value) {
      return new Double2ShortSortedMaps.Singleton(key, value);
   }

   public static Double2ShortSortedMap singleton(Double key, Short value, DoubleComparator comparator) {
      return new Double2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2ShortSortedMap singleton(double key, short value) {
      return new Double2ShortSortedMaps.Singleton(key, value);
   }

   public static Double2ShortSortedMap singleton(double key, short value, DoubleComparator comparator) {
      return new Double2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2ShortSortedMap synchronize(Double2ShortSortedMap m) {
      return new Double2ShortSortedMaps.SynchronizedSortedMap(m);
   }

   public static Double2ShortSortedMap synchronize(Double2ShortSortedMap m, Object sync) {
      return new Double2ShortSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Double2ShortSortedMap unmodifiable(Double2ShortSortedMap m) {
      return new Double2ShortSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Double2ShortMaps.EmptyMap implements Double2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet keySet() {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public Double2ShortSortedMap subMap(double from, double to) {
         return Double2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2ShortSortedMap headMap(double to) {
         return Double2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2ShortSortedMap tailMap(double from) {
         return Double2ShortSortedMaps.EMPTY_MAP;
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
      public Double2ShortSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ShortSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ShortSortedMap subMap(Double ofrom, Double oto) {
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

   public static class Singleton extends Double2ShortMaps.Singleton implements Double2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleComparator comparator;

      protected Singleton(double key, short value, DoubleComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(double key, short value) {
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
      public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractDouble2ShortMap.BasicEntry(this.key, this.value), Double2ShortSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Double2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Short>> entrySet() {
         return this.double2ShortEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ShortSortedMap subMap(double from, double to) {
         return (Double2ShortSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Double2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2ShortSortedMap headMap(double to) {
         return (Double2ShortSortedMap)(this.compare(this.key, to) < 0 ? this : Double2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2ShortSortedMap tailMap(double from) {
         return (Double2ShortSortedMap)(this.compare(from, this.key) <= 0 ? this : Double2ShortSortedMaps.EMPTY_MAP);
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
      public Double2ShortSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ShortSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ShortSortedMap subMap(Double ofrom, Double oto) {
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

   public static class SynchronizedSortedMap extends Double2ShortMaps.SynchronizedMap implements Double2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ShortSortedMap sortedMap;

      protected SynchronizedSortedMap(Double2ShortSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Double2ShortSortedMap m) {
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
      public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Double2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Short>> entrySet() {
         return this.double2ShortEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ShortSortedMap subMap(double from, double to) {
         return new Double2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Double2ShortSortedMap headMap(double to) {
         return new Double2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Double2ShortSortedMap tailMap(double from) {
         return new Double2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Double2ShortSortedMap subMap(Double from, Double to) {
         return new Double2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Double2ShortSortedMap headMap(Double to) {
         return new Double2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Double2ShortSortedMap tailMap(Double from) {
         return new Double2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Double2ShortMaps.UnmodifiableMap implements Double2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ShortSortedMap sortedMap;

      protected UnmodifiableSortedMap(Double2ShortSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Double2ShortMap.Entry> double2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2ShortEntrySet());
         }

         return (ObjectSortedSet<Double2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Short>> entrySet() {
         return this.double2ShortEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ShortSortedMap subMap(double from, double to) {
         return new Double2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Double2ShortSortedMap headMap(double to) {
         return new Double2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Double2ShortSortedMap tailMap(double from) {
         return new Double2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Double2ShortSortedMap subMap(Double from, Double to) {
         return new Double2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Double2ShortSortedMap headMap(Double to) {
         return new Double2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Double2ShortSortedMap tailMap(Double from) {
         return new Double2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
