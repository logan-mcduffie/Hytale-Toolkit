package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Double2ByteSortedMaps {
   public static final Double2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Double2ByteSortedMaps.EmptySortedMap();

   private Double2ByteSortedMaps() {
   }

   public static Comparator<? super Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().doubleValue(), y.getKey().doubleValue());
   }

   public static ObjectBidirectionalIterator<Double2ByteMap.Entry> fastIterator(Double2ByteSortedMap map) {
      ObjectSortedSet<Double2ByteMap.Entry> entries = map.double2ByteEntrySet();
      return entries instanceof Double2ByteSortedMap.FastSortedEntrySet
         ? ((Double2ByteSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Double2ByteMap.Entry> fastIterable(Double2ByteSortedMap map) {
      ObjectSortedSet<Double2ByteMap.Entry> entries = map.double2ByteEntrySet();
      return (ObjectBidirectionalIterable<Double2ByteMap.Entry>)(entries instanceof Double2ByteSortedMap.FastSortedEntrySet
         ? ((Double2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Double2ByteSortedMap singleton(Double key, Byte value) {
      return new Double2ByteSortedMaps.Singleton(key, value);
   }

   public static Double2ByteSortedMap singleton(Double key, Byte value, DoubleComparator comparator) {
      return new Double2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2ByteSortedMap singleton(double key, byte value) {
      return new Double2ByteSortedMaps.Singleton(key, value);
   }

   public static Double2ByteSortedMap singleton(double key, byte value, DoubleComparator comparator) {
      return new Double2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Double2ByteSortedMap synchronize(Double2ByteSortedMap m) {
      return new Double2ByteSortedMaps.SynchronizedSortedMap(m);
   }

   public static Double2ByteSortedMap synchronize(Double2ByteSortedMap m, Object sync) {
      return new Double2ByteSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Double2ByteSortedMap unmodifiable(Double2ByteSortedMap m) {
      return new Double2ByteSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Double2ByteMaps.EmptyMap implements Double2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet keySet() {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public Double2ByteSortedMap subMap(double from, double to) {
         return Double2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2ByteSortedMap headMap(double to) {
         return Double2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Double2ByteSortedMap tailMap(double from) {
         return Double2ByteSortedMaps.EMPTY_MAP;
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
      public Double2ByteSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ByteSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ByteSortedMap subMap(Double ofrom, Double oto) {
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

   public static class Singleton extends Double2ByteMaps.Singleton implements Double2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleComparator comparator;

      protected Singleton(double key, byte value, DoubleComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(double key, byte value) {
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
      public ObjectSortedSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractDouble2ByteMap.BasicEntry(this.key, this.value), Double2ByteSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Double2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ByteSortedMap subMap(double from, double to) {
         return (Double2ByteSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Double2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2ByteSortedMap headMap(double to) {
         return (Double2ByteSortedMap)(this.compare(this.key, to) < 0 ? this : Double2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Double2ByteSortedMap tailMap(double from) {
         return (Double2ByteSortedMap)(this.compare(from, this.key) <= 0 ? this : Double2ByteSortedMaps.EMPTY_MAP);
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
      public Double2ByteSortedMap headMap(Double oto) {
         return this.headMap(oto.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ByteSortedMap tailMap(Double ofrom) {
         return this.tailMap(ofrom.doubleValue());
      }

      @Deprecated
      @Override
      public Double2ByteSortedMap subMap(Double ofrom, Double oto) {
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

   public static class SynchronizedSortedMap extends Double2ByteMaps.SynchronizedMap implements Double2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ByteSortedMap sortedMap;

      protected SynchronizedSortedMap(Double2ByteSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Double2ByteSortedMap m) {
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
      public ObjectSortedSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Double2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ByteSortedMap subMap(double from, double to) {
         return new Double2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Double2ByteSortedMap headMap(double to) {
         return new Double2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Double2ByteSortedMap tailMap(double from) {
         return new Double2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Double2ByteSortedMap subMap(Double from, Double to) {
         return new Double2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Double2ByteSortedMap headMap(Double to) {
         return new Double2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Double2ByteSortedMap tailMap(Double from) {
         return new Double2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Double2ByteMaps.UnmodifiableMap implements Double2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ByteSortedMap sortedMap;

      protected UnmodifiableSortedMap(Double2ByteSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2ByteEntrySet());
         }

         return (ObjectSortedSet<Double2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
      }

      @Override
      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (DoubleSortedSet)this.keys;
      }

      @Override
      public Double2ByteSortedMap subMap(double from, double to) {
         return new Double2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Double2ByteSortedMap headMap(double to) {
         return new Double2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Double2ByteSortedMap tailMap(double from) {
         return new Double2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Double2ByteSortedMap subMap(Double from, Double to) {
         return new Double2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Double2ByteSortedMap headMap(Double to) {
         return new Double2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Double2ByteSortedMap tailMap(Double from) {
         return new Double2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
