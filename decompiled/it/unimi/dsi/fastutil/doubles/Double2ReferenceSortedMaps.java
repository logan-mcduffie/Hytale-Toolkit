package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Double2ReferenceSortedMaps {
   public static final Double2ReferenceSortedMaps.EmptySortedMap EMPTY_MAP = new Double2ReferenceSortedMaps.EmptySortedMap();

   private Double2ReferenceSortedMaps() {
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().doubleValue(), y.getKey().doubleValue());
   }

   public static <V> ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> fastIterator(Double2ReferenceSortedMap<V> map) {
      ObjectSortedSet<Double2ReferenceMap.Entry<V>> entries = map.double2ReferenceEntrySet();
      return entries instanceof Double2ReferenceSortedMap.FastSortedEntrySet
         ? ((Double2ReferenceSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Double2ReferenceMap.Entry<V>> fastIterable(Double2ReferenceSortedMap<V> map) {
      ObjectSortedSet<Double2ReferenceMap.Entry<V>> entries = map.double2ReferenceEntrySet();
      return (ObjectBidirectionalIterable<Double2ReferenceMap.Entry<V>>)(entries instanceof Double2ReferenceSortedMap.FastSortedEntrySet
         ? ((Double2ReferenceSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <V> Double2ReferenceSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Double2ReferenceSortedMap<V> singleton(Double key, V value) {
      return new Double2ReferenceSortedMaps.Singleton<>(key, value);
   }

   public static <V> Double2ReferenceSortedMap<V> singleton(Double key, V value, DoubleComparator comparator) {
      return new Double2ReferenceSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Double2ReferenceSortedMap<V> singleton(double key, V value) {
      return new Double2ReferenceSortedMaps.Singleton<>(key, value);
   }

   public static <V> Double2ReferenceSortedMap<V> singleton(double key, V value, DoubleComparator comparator) {
      return new Double2ReferenceSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Double2ReferenceSortedMap<V> synchronize(Double2ReferenceSortedMap<V> m) {
      return new Double2ReferenceSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <V> Double2ReferenceSortedMap<V> synchronize(Double2ReferenceSortedMap<V> m, Object sync) {
      return new Double2ReferenceSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <V> Double2ReferenceSortedMap<V> unmodifiable(Double2ReferenceSortedMap<? extends V> m) {
      return new Double2ReferenceSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<V> extends Double2ReferenceMaps.EmptyMap<V> implements Double2ReferenceSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet keySet() {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public Double2ReferenceSortedMap<V> subMap(double from, double to) {
         return Double2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2ReferenceSortedMap<V> headMap(double to) {
         return Double2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2ReferenceSortedMap<V> tailMap(double from) {
         return Double2ReferenceSortedMaps.EMPTY_MAP;
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
      public Double2ReferenceSortedMap<V> headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ReferenceSortedMap<V> tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ReferenceSortedMap<V> subMap(Double ofrom, Double oto) {
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

   public static class Singleton<V> extends Double2ReferenceMaps.Singleton<V> implements Double2ReferenceSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleComparator comparator;

      protected Singleton(double key, V value, DoubleComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(double key, V value) {
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
      public ObjectSortedSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractDouble2ReferenceMap.BasicEntry<>(this.key, this.value), Double2ReferenceSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Double2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, V>> entrySet() {
         return this.double2ReferenceEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ReferenceSortedMap<V> subMap(double from, double to) {
         return (Double2ReferenceSortedMap<V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0
            ? this
            : Double2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2ReferenceSortedMap<V> headMap(double to) {
         return (Double2ReferenceSortedMap<V>)(this.compare(this.key, to) < 0 ? this : Double2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2ReferenceSortedMap<V> tailMap(double from) {
         return (Double2ReferenceSortedMap<V>)(this.compare(from, this.key) <= 0 ? this : Double2ReferenceSortedMaps.EMPTY_MAP);
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
      public Double2ReferenceSortedMap<V> headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ReferenceSortedMap<V> tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ReferenceSortedMap<V> subMap(Double ofrom, Double oto) {
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

   public static class SynchronizedSortedMap<V> extends Double2ReferenceMaps.SynchronizedMap<V> implements Double2ReferenceSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ReferenceSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Double2ReferenceSortedMap<V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Double2ReferenceSortedMap<V> m) {
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
      public ObjectSortedSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2ReferenceEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Double2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, V>> entrySet() {
         return this.double2ReferenceEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ReferenceSortedMap<V> subMap(double from, double to) {
         return new Double2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Double2ReferenceSortedMap<V> headMap(double to) {
         return new Double2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Double2ReferenceSortedMap<V> tailMap(double from) {
         return new Double2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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
      public Double2ReferenceSortedMap<V> subMap(Double from, Double to) {
         return new Double2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Double2ReferenceSortedMap<V> headMap(Double to) {
         return new Double2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Double2ReferenceSortedMap<V> tailMap(Double from) {
         return new Double2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap<V> extends Double2ReferenceMaps.UnmodifiableMap<V> implements Double2ReferenceSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ReferenceSortedMap<? extends V> sortedMap;

      protected UnmodifiableSortedMap(Double2ReferenceSortedMap<? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Double2ReferenceMap.Entry<V>>)this.sortedMap.double2ReferenceEntrySet());
         }

         return (ObjectSortedSet<Double2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, V>> entrySet() {
         return this.double2ReferenceEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ReferenceSortedMap<V> subMap(double from, double to) {
         return new Double2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Double2ReferenceSortedMap<V> headMap(double to) {
         return new Double2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Double2ReferenceSortedMap<V> tailMap(double from) {
         return new Double2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
      public Double2ReferenceSortedMap<V> subMap(Double from, Double to) {
         return new Double2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Double2ReferenceSortedMap<V> headMap(Double to) {
         return new Double2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Double2ReferenceSortedMap<V> tailMap(Double from) {
         return new Double2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }
   }
}
