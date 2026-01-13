package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Double2BooleanSortedMaps {
   public static final Double2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Double2BooleanSortedMaps.EmptySortedMap();

   private Double2BooleanSortedMaps() {
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().doubleValue(), y.getKey().doubleValue());
   }

   public static ObjectBidirectionalIterator<Double2BooleanMap.Entry> fastIterator(Double2BooleanSortedMap map) {
      ObjectSortedSet<Double2BooleanMap.Entry> entries = map.double2BooleanEntrySet();
      return entries instanceof Double2BooleanSortedMap.FastSortedEntrySet
         ? ((Double2BooleanSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Double2BooleanMap.Entry> fastIterable(Double2BooleanSortedMap map) {
      ObjectSortedSet<Double2BooleanMap.Entry> entries = map.double2BooleanEntrySet();
      return (ObjectBidirectionalIterable<Double2BooleanMap.Entry>)(entries instanceof Double2BooleanSortedMap.FastSortedEntrySet
         ? ((Double2BooleanSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Double2BooleanSortedMap singleton(Double key, Boolean value) {
      return new Double2BooleanSortedMaps.Singleton(key, value);
   }

   public static Double2BooleanSortedMap singleton(Double key, Boolean value, DoubleComparator comparator) {
      return new Double2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2BooleanSortedMap singleton(double key, boolean value) {
      return new Double2BooleanSortedMaps.Singleton(key, value);
   }

   public static Double2BooleanSortedMap singleton(double key, boolean value, DoubleComparator comparator) {
      return new Double2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2BooleanSortedMap synchronize(Double2BooleanSortedMap m) {
      return new Double2BooleanSortedMaps.SynchronizedSortedMap(m);
   }

   public static Double2BooleanSortedMap synchronize(Double2BooleanSortedMap m, Object sync) {
      return new Double2BooleanSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Double2BooleanSortedMap unmodifiable(Double2BooleanSortedMap m) {
      return new Double2BooleanSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Double2BooleanMaps.EmptyMap implements Double2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet keySet() {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public Double2BooleanSortedMap subMap(double from, double to) {
         return Double2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2BooleanSortedMap headMap(double to) {
         return Double2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2BooleanSortedMap tailMap(double from) {
         return Double2BooleanSortedMaps.EMPTY_MAP;
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
      public Double2BooleanSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2BooleanSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2BooleanSortedMap subMap(Double ofrom, Double oto) {
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

   public static class Singleton extends Double2BooleanMaps.Singleton implements Double2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleComparator comparator;

      protected Singleton(double key, boolean value, DoubleComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(double key, boolean value) {
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
      public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractDouble2BooleanMap.BasicEntry(this.key, this.value), Double2BooleanSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Double2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2BooleanSortedMap subMap(double from, double to) {
         return (Double2BooleanSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Double2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2BooleanSortedMap headMap(double to) {
         return (Double2BooleanSortedMap)(this.compare(this.key, to) < 0 ? this : Double2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2BooleanSortedMap tailMap(double from) {
         return (Double2BooleanSortedMap)(this.compare(from, this.key) <= 0 ? this : Double2BooleanSortedMaps.EMPTY_MAP);
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
      public Double2BooleanSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2BooleanSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2BooleanSortedMap subMap(Double ofrom, Double oto) {
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

   public static class SynchronizedSortedMap extends Double2BooleanMaps.SynchronizedMap implements Double2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Double2BooleanSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Double2BooleanSortedMap m) {
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
      public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Double2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2BooleanSortedMap subMap(double from, double to) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Double2BooleanSortedMap headMap(double to) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Double2BooleanSortedMap tailMap(double from) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Double2BooleanSortedMap subMap(Double from, Double to) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Double2BooleanSortedMap headMap(Double to) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Double2BooleanSortedMap tailMap(Double from) {
         return new Double2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Double2BooleanMaps.UnmodifiableMap implements Double2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Double2BooleanSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Double2BooleanMap.Entry> double2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2BooleanEntrySet());
         }

         return (ObjectSortedSet<Double2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Boolean>> entrySet() {
         return this.double2BooleanEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2BooleanSortedMap subMap(double from, double to) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Double2BooleanSortedMap headMap(double to) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Double2BooleanSortedMap tailMap(double from) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Double2BooleanSortedMap subMap(Double from, Double to) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Double2BooleanSortedMap headMap(Double to) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Double2BooleanSortedMap tailMap(Double from) {
         return new Double2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
