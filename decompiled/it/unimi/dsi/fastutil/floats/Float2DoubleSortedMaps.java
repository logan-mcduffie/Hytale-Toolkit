package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Float2DoubleSortedMaps {
   public static final Float2DoubleSortedMaps.EmptySortedMap EMPTY_MAP = new Float2DoubleSortedMaps.EmptySortedMap();

   private Float2DoubleSortedMaps() {
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().floatValue(), y.getKey().floatValue());
   }

   public static ObjectBidirectionalIterator<Float2DoubleMap.Entry> fastIterator(Float2DoubleSortedMap map) {
      ObjectSortedSet<Float2DoubleMap.Entry> entries = map.float2DoubleEntrySet();
      return entries instanceof Float2DoubleSortedMap.FastSortedEntrySet
         ? ((Float2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Float2DoubleMap.Entry> fastIterable(Float2DoubleSortedMap map) {
      ObjectSortedSet<Float2DoubleMap.Entry> entries = map.float2DoubleEntrySet();
      return (ObjectBidirectionalIterable<Float2DoubleMap.Entry>)(entries instanceof Float2DoubleSortedMap.FastSortedEntrySet
         ? ((Float2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Float2DoubleSortedMap singleton(Float key, Double value) {
      return new Float2DoubleSortedMaps.Singleton(key, value);
   }

   public static Float2DoubleSortedMap singleton(Float key, Double value, FloatComparator comparator) {
      return new Float2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2DoubleSortedMap singleton(float key, double value) {
      return new Float2DoubleSortedMaps.Singleton(key, value);
   }

   public static Float2DoubleSortedMap singleton(float key, double value, FloatComparator comparator) {
      return new Float2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2DoubleSortedMap synchronize(Float2DoubleSortedMap m) {
      return new Float2DoubleSortedMaps.SynchronizedSortedMap(m);
   }

   public static Float2DoubleSortedMap synchronize(Float2DoubleSortedMap m, Object sync) {
      return new Float2DoubleSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Float2DoubleSortedMap unmodifiable(Float2DoubleSortedMap m) {
      return new Float2DoubleSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Float2DoubleMaps.EmptyMap implements Float2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Double>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      @Override
      public Float2DoubleSortedMap subMap(float from, float to) {
         return Float2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2DoubleSortedMap headMap(float to) {
         return Float2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2DoubleSortedMap tailMap(float from) {
         return Float2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public float firstFloatKey() {
         throw new NoSuchElementException();
      }

      @Override
      public float lastFloatKey() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap subMap(Float ofrom, Float oto) {
         return this.subMap(ofrom.floatValue(), oto.floatValue());
      }

      @Deprecated
      @Override
      public Float firstKey() {
         return this.firstFloatKey();
      }

      @Deprecated
      @Override
      public Float lastKey() {
         return this.lastFloatKey();
      }
   }

   public static class Singleton extends Float2DoubleMaps.Singleton implements Float2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float key, double value, FloatComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(float key, double value) {
         this(key, value, null);
      }

      final int compare(float k1, float k2) {
         return this.comparator == null ? Float.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public FloatComparator comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractFloat2DoubleMap.BasicEntry(this.key, this.value), Float2DoubleSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Float2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Double>> entrySet() {
         return this.float2DoubleEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2DoubleSortedMap subMap(float from, float to) {
         return (Float2DoubleSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Float2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2DoubleSortedMap headMap(float to) {
         return (Float2DoubleSortedMap)(this.compare(this.key, to) < 0 ? this : Float2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2DoubleSortedMap tailMap(float from) {
         return (Float2DoubleSortedMap)(this.compare(from, this.key) <= 0 ? this : Float2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public float firstFloatKey() {
         return this.key;
      }

      @Override
      public float lastFloatKey() {
         return this.key;
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap subMap(Float ofrom, Float oto) {
         return this.subMap(ofrom.floatValue(), oto.floatValue());
      }

      @Deprecated
      @Override
      public Float firstKey() {
         return this.firstFloatKey();
      }

      @Deprecated
      @Override
      public Float lastKey() {
         return this.lastFloatKey();
      }
   }

   public static class SynchronizedSortedMap extends Float2DoubleMaps.SynchronizedMap implements Float2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2DoubleSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2DoubleSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Float2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         synchronized (this.sync) {
            return this.sortedMap.comparator();
         }
      }

      @Override
      public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2DoubleEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Float2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Double>> entrySet() {
         return this.float2DoubleEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2DoubleSortedMap subMap(float from, float to) {
         return new Float2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Float2DoubleSortedMap headMap(float to) {
         return new Float2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Float2DoubleSortedMap tailMap(float from) {
         return new Float2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }

      @Override
      public float firstFloatKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstFloatKey();
         }
      }

      @Override
      public float lastFloatKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastFloatKey();
         }
      }

      @Deprecated
      @Override
      public Float firstKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      @Deprecated
      @Override
      public Float lastKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap subMap(Float from, Float to) {
         return new Float2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap headMap(Float to) {
         return new Float2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap tailMap(Float from) {
         return new Float2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Float2DoubleMaps.UnmodifiableMap implements Float2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2DoubleSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2DoubleEntrySet());
         }

         return (ObjectSortedSet<Float2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Double>> entrySet() {
         return this.float2DoubleEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2DoubleSortedMap subMap(float from, float to) {
         return new Float2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Float2DoubleSortedMap headMap(float to) {
         return new Float2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Float2DoubleSortedMap tailMap(float from) {
         return new Float2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }

      @Override
      public float firstFloatKey() {
         return this.sortedMap.firstFloatKey();
      }

      @Override
      public float lastFloatKey() {
         return this.sortedMap.lastFloatKey();
      }

      @Deprecated
      @Override
      public Float firstKey() {
         return this.sortedMap.firstKey();
      }

      @Deprecated
      @Override
      public Float lastKey() {
         return this.sortedMap.lastKey();
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap subMap(Float from, Float to) {
         return new Float2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap headMap(Float to) {
         return new Float2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Float2DoubleSortedMap tailMap(Float from) {
         return new Float2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
