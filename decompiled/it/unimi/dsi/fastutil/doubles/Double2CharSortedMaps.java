package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Double2CharSortedMaps {
   public static final Double2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Double2CharSortedMaps.EmptySortedMap();

   private Double2CharSortedMaps() {
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().doubleValue(), y.getKey().doubleValue());
   }

   public static ObjectBidirectionalIterator<Double2CharMap.Entry> fastIterator(Double2CharSortedMap map) {
      ObjectSortedSet<Double2CharMap.Entry> entries = map.double2CharEntrySet();
      return entries instanceof Double2CharSortedMap.FastSortedEntrySet
         ? ((Double2CharSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Double2CharMap.Entry> fastIterable(Double2CharSortedMap map) {
      ObjectSortedSet<Double2CharMap.Entry> entries = map.double2CharEntrySet();
      return (ObjectBidirectionalIterable<Double2CharMap.Entry>)(entries instanceof Double2CharSortedMap.FastSortedEntrySet
         ? ((Double2CharSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Double2CharSortedMap singleton(Double key, Character value) {
      return new Double2CharSortedMaps.Singleton(key, value);
   }

   public static Double2CharSortedMap singleton(Double key, Character value, DoubleComparator comparator) {
      return new Double2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2CharSortedMap singleton(double key, char value) {
      return new Double2CharSortedMaps.Singleton(key, value);
   }

   public static Double2CharSortedMap singleton(double key, char value, DoubleComparator comparator) {
      return new Double2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2CharSortedMap synchronize(Double2CharSortedMap m) {
      return new Double2CharSortedMaps.SynchronizedSortedMap(m);
   }

   public static Double2CharSortedMap synchronize(Double2CharSortedMap m, Object sync) {
      return new Double2CharSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Double2CharSortedMap unmodifiable(Double2CharSortedMap m) {
      return new Double2CharSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Double2CharMaps.EmptyMap implements Double2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Double2CharMap.Entry> double2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet keySet() {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public Double2CharSortedMap subMap(double from, double to) {
         return Double2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2CharSortedMap headMap(double to) {
         return Double2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2CharSortedMap tailMap(double from) {
         return Double2CharSortedMaps.EMPTY_MAP;
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
      public Double2CharSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2CharSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2CharSortedMap subMap(Double ofrom, Double oto) {
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

   public static class Singleton extends Double2CharMaps.Singleton implements Double2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleComparator comparator;

      protected Singleton(double key, char value, DoubleComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(double key, char value) {
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
      public ObjectSortedSet<Double2CharMap.Entry> double2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractDouble2CharMap.BasicEntry(this.key, this.value), Double2CharSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Double2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Character>> entrySet() {
         return this.double2CharEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2CharSortedMap subMap(double from, double to) {
         return (Double2CharSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Double2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2CharSortedMap headMap(double to) {
         return (Double2CharSortedMap)(this.compare(this.key, to) < 0 ? this : Double2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2CharSortedMap tailMap(double from) {
         return (Double2CharSortedMap)(this.compare(from, this.key) <= 0 ? this : Double2CharSortedMaps.EMPTY_MAP);
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
      public Double2CharSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2CharSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2CharSortedMap subMap(Double ofrom, Double oto) {
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

   public static class SynchronizedSortedMap extends Double2CharMaps.SynchronizedMap implements Double2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Double2CharSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Double2CharSortedMap m) {
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
      public ObjectSortedSet<Double2CharMap.Entry> double2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Double2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Character>> entrySet() {
         return this.double2CharEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2CharSortedMap subMap(double from, double to) {
         return new Double2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Double2CharSortedMap headMap(double to) {
         return new Double2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Double2CharSortedMap tailMap(double from) {
         return new Double2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Double2CharSortedMap subMap(Double from, Double to) {
         return new Double2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Double2CharSortedMap headMap(Double to) {
         return new Double2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Double2CharSortedMap tailMap(Double from) {
         return new Double2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Double2CharMaps.UnmodifiableMap implements Double2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Double2CharSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Double2CharMap.Entry> double2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2CharEntrySet());
         }

         return (ObjectSortedSet<Double2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Character>> entrySet() {
         return this.double2CharEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2CharSortedMap subMap(double from, double to) {
         return new Double2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Double2CharSortedMap headMap(double to) {
         return new Double2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Double2CharSortedMap tailMap(double from) {
         return new Double2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Double2CharSortedMap subMap(Double from, Double to) {
         return new Double2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Double2CharSortedMap headMap(Double to) {
         return new Double2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Double2CharSortedMap tailMap(Double from) {
         return new Double2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
