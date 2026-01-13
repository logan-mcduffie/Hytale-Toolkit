package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Double2IntSortedMaps {
   public static final Double2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Double2IntSortedMaps.EmptySortedMap();

   private Double2IntSortedMaps() {
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().doubleValue(), y.getKey().doubleValue());
   }

   public static ObjectBidirectionalIterator<Double2IntMap.Entry> fastIterator(Double2IntSortedMap map) {
      ObjectSortedSet<Double2IntMap.Entry> entries = map.double2IntEntrySet();
      return entries instanceof Double2IntSortedMap.FastSortedEntrySet ? ((Double2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Double2IntMap.Entry> fastIterable(Double2IntSortedMap map) {
      ObjectSortedSet<Double2IntMap.Entry> entries = map.double2IntEntrySet();
      return (ObjectBidirectionalIterable<Double2IntMap.Entry>)(entries instanceof Double2IntSortedMap.FastSortedEntrySet
         ? ((Double2IntSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Double2IntSortedMap singleton(Double key, Integer value) {
      return new Double2IntSortedMaps.Singleton(key, value);
   }

   public static Double2IntSortedMap singleton(Double key, Integer value, DoubleComparator comparator) {
      return new Double2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2IntSortedMap singleton(double key, int value) {
      return new Double2IntSortedMaps.Singleton(key, value);
   }

   public static Double2IntSortedMap singleton(double key, int value, DoubleComparator comparator) {
      return new Double2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2IntSortedMap synchronize(Double2IntSortedMap m) {
      return new Double2IntSortedMaps.SynchronizedSortedMap(m);
   }

   public static Double2IntSortedMap synchronize(Double2IntSortedMap m, Object sync) {
      return new Double2IntSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Double2IntSortedMap unmodifiable(Double2IntSortedMap m) {
      return new Double2IntSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Double2IntMaps.EmptyMap implements Double2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Double2IntMap.Entry> double2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet keySet() {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public Double2IntSortedMap subMap(double from, double to) {
         return Double2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2IntSortedMap headMap(double to) {
         return Double2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2IntSortedMap tailMap(double from) {
         return Double2IntSortedMaps.EMPTY_MAP;
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
      public Double2IntSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2IntSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2IntSortedMap subMap(Double ofrom, Double oto) {
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

   public static class Singleton extends Double2IntMaps.Singleton implements Double2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleComparator comparator;

      protected Singleton(double key, int value, DoubleComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(double key, int value) {
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
      public ObjectSortedSet<Double2IntMap.Entry> double2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractDouble2IntMap.BasicEntry(this.key, this.value), Double2IntSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Double2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2IntSortedMap subMap(double from, double to) {
         return (Double2IntSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Double2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2IntSortedMap headMap(double to) {
         return (Double2IntSortedMap)(this.compare(this.key, to) < 0 ? this : Double2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2IntSortedMap tailMap(double from) {
         return (Double2IntSortedMap)(this.compare(from, this.key) <= 0 ? this : Double2IntSortedMaps.EMPTY_MAP);
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
      public Double2IntSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2IntSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2IntSortedMap subMap(Double ofrom, Double oto) {
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

   public static class SynchronizedSortedMap extends Double2IntMaps.SynchronizedMap implements Double2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntSortedMap sortedMap;

      protected SynchronizedSortedMap(Double2IntSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Double2IntSortedMap m) {
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
      public ObjectSortedSet<Double2IntMap.Entry> double2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Double2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2IntSortedMap subMap(double from, double to) {
         return new Double2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Double2IntSortedMap headMap(double to) {
         return new Double2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Double2IntSortedMap tailMap(double from) {
         return new Double2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Double2IntSortedMap subMap(Double from, Double to) {
         return new Double2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Double2IntSortedMap headMap(Double to) {
         return new Double2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Double2IntSortedMap tailMap(Double from) {
         return new Double2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Double2IntMaps.UnmodifiableMap implements Double2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntSortedMap sortedMap;

      protected UnmodifiableSortedMap(Double2IntSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Double2IntMap.Entry> double2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2IntEntrySet());
         }

         return (ObjectSortedSet<Double2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2IntSortedMap subMap(double from, double to) {
         return new Double2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Double2IntSortedMap headMap(double to) {
         return new Double2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Double2IntSortedMap tailMap(double from) {
         return new Double2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Double2IntSortedMap subMap(Double from, Double to) {
         return new Double2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Double2IntSortedMap headMap(Double to) {
         return new Double2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Double2IntSortedMap tailMap(Double from) {
         return new Double2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
