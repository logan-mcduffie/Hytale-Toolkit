package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Double2ObjectSortedMaps {
   public static final Double2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Double2ObjectSortedMaps.EmptySortedMap();

   private Double2ObjectSortedMaps() {
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().doubleValue(), y.getKey().doubleValue());
   }

   public static <V> ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> fastIterator(Double2ObjectSortedMap<V> map) {
      ObjectSortedSet<Double2ObjectMap.Entry<V>> entries = map.double2ObjectEntrySet();
      return entries instanceof Double2ObjectSortedMap.FastSortedEntrySet
         ? ((Double2ObjectSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Double2ObjectMap.Entry<V>> fastIterable(Double2ObjectSortedMap<V> map) {
      ObjectSortedSet<Double2ObjectMap.Entry<V>> entries = map.double2ObjectEntrySet();
      return (ObjectBidirectionalIterable<Double2ObjectMap.Entry<V>>)(entries instanceof Double2ObjectSortedMap.FastSortedEntrySet
         ? ((Double2ObjectSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <V> Double2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Double2ObjectSortedMap<V> singleton(Double key, V value) {
      return new Double2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Double2ObjectSortedMap<V> singleton(Double key, V value, DoubleComparator comparator) {
      return new Double2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Double2ObjectSortedMap<V> singleton(double key, V value) {
      return new Double2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Double2ObjectSortedMap<V> singleton(double key, V value, DoubleComparator comparator) {
      return new Double2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Double2ObjectSortedMap<V> synchronize(Double2ObjectSortedMap<V> m) {
      return new Double2ObjectSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <V> Double2ObjectSortedMap<V> synchronize(Double2ObjectSortedMap<V> m, Object sync) {
      return new Double2ObjectSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <V> Double2ObjectSortedMap<V> unmodifiable(Double2ObjectSortedMap<? extends V> m) {
      return new Double2ObjectSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<V> extends Double2ObjectMaps.EmptyMap<V> implements Double2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
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
      public Double2ObjectSortedMap<V> subMap(double from, double to) {
         return Double2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2ObjectSortedMap<V> headMap(double to) {
         return Double2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2ObjectSortedMap<V> tailMap(double from) {
         return Double2ObjectSortedMaps.EMPTY_MAP;
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
      public Double2ObjectSortedMap<V> headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ObjectSortedMap<V> tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ObjectSortedMap<V> subMap(Double ofrom, Double oto) {
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

   public static class Singleton<V> extends Double2ObjectMaps.Singleton<V> implements Double2ObjectSortedMap<V>, Serializable, Cloneable {
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
      public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractDouble2ObjectMap.BasicEntry<>(this.key, this.value), Double2ObjectSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Double2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, V>> entrySet() {
         return this.double2ObjectEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ObjectSortedMap<V> subMap(double from, double to) {
         return (Double2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Double2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2ObjectSortedMap<V> headMap(double to) {
         return (Double2ObjectSortedMap<V>)(this.compare(this.key, to) < 0 ? this : Double2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2ObjectSortedMap<V> tailMap(double from) {
         return (Double2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 ? this : Double2ObjectSortedMaps.EMPTY_MAP);
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
      public Double2ObjectSortedMap<V> headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ObjectSortedMap<V> tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ObjectSortedMap<V> subMap(Double ofrom, Double oto) {
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

   public static class SynchronizedSortedMap<V> extends Double2ObjectMaps.SynchronizedMap<V> implements Double2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Double2ObjectSortedMap<V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Double2ObjectSortedMap<V> m) {
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
      public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Double2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, V>> entrySet() {
         return this.double2ObjectEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ObjectSortedMap<V> subMap(double from, double to) {
         return new Double2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Double2ObjectSortedMap<V> headMap(double to) {
         return new Double2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Double2ObjectSortedMap<V> tailMap(double from) {
         return new Double2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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
      public Double2ObjectSortedMap<V> subMap(Double from, Double to) {
         return new Double2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Double2ObjectSortedMap<V> headMap(Double to) {
         return new Double2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Double2ObjectSortedMap<V> tailMap(Double from) {
         return new Double2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap<V> extends Double2ObjectMaps.UnmodifiableMap<V> implements Double2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ObjectSortedMap<? extends V> sortedMap;

      protected UnmodifiableSortedMap(Double2ObjectSortedMap<? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Double2ObjectMap.Entry<V>>)this.sortedMap.double2ObjectEntrySet());
         }

         return (ObjectSortedSet<Double2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, V>> entrySet() {
         return this.double2ObjectEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ObjectSortedMap<V> subMap(double from, double to) {
         return new Double2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Double2ObjectSortedMap<V> headMap(double to) {
         return new Double2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Double2ObjectSortedMap<V> tailMap(double from) {
         return new Double2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
      public Double2ObjectSortedMap<V> subMap(Double from, Double to) {
         return new Double2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Double2ObjectSortedMap<V> headMap(Double to) {
         return new Double2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Double2ObjectSortedMap<V> tailMap(Double from) {
         return new Double2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }
   }
}
