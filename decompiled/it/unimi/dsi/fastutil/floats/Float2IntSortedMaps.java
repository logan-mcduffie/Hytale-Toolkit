package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Float2IntSortedMaps {
   public static final Float2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Float2IntSortedMaps.EmptySortedMap();

   private Float2IntSortedMaps() {
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().floatValue(), y.getKey().floatValue());
   }

   public static ObjectBidirectionalIterator<Float2IntMap.Entry> fastIterator(Float2IntSortedMap map) {
      ObjectSortedSet<Float2IntMap.Entry> entries = map.float2IntEntrySet();
      return entries instanceof Float2IntSortedMap.FastSortedEntrySet ? ((Float2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Float2IntMap.Entry> fastIterable(Float2IntSortedMap map) {
      ObjectSortedSet<Float2IntMap.Entry> entries = map.float2IntEntrySet();
      return (ObjectBidirectionalIterable<Float2IntMap.Entry>)(entries instanceof Float2IntSortedMap.FastSortedEntrySet
         ? ((Float2IntSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Float2IntSortedMap singleton(Float key, Integer value) {
      return new Float2IntSortedMaps.Singleton(key, value);
   }

   public static Float2IntSortedMap singleton(Float key, Integer value, FloatComparator comparator) {
      return new Float2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2IntSortedMap singleton(float key, int value) {
      return new Float2IntSortedMaps.Singleton(key, value);
   }

   public static Float2IntSortedMap singleton(float key, int value, FloatComparator comparator) {
      return new Float2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2IntSortedMap synchronize(Float2IntSortedMap m) {
      return new Float2IntSortedMaps.SynchronizedSortedMap(m);
   }

   public static Float2IntSortedMap synchronize(Float2IntSortedMap m, Object sync) {
      return new Float2IntSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Float2IntSortedMap unmodifiable(Float2IntSortedMap m) {
      return new Float2IntSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Float2IntMaps.EmptyMap implements Float2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Float2IntMap.Entry> float2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      @Override
      public Float2IntSortedMap subMap(float from, float to) {
         return Float2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2IntSortedMap headMap(float to) {
         return Float2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2IntSortedMap tailMap(float from) {
         return Float2IntSortedMaps.EMPTY_MAP;
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
      public Float2IntSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2IntSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2IntSortedMap subMap(Float ofrom, Float oto) {
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

   public static class Singleton extends Float2IntMaps.Singleton implements Float2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float key, int value, FloatComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(float key, int value) {
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
      public ObjectSortedSet<Float2IntMap.Entry> float2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractFloat2IntMap.BasicEntry(this.key, this.value), Float2IntSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Float2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Integer>> entrySet() {
         return this.float2IntEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2IntSortedMap subMap(float from, float to) {
         return (Float2IntSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Float2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2IntSortedMap headMap(float to) {
         return (Float2IntSortedMap)(this.compare(this.key, to) < 0 ? this : Float2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2IntSortedMap tailMap(float from) {
         return (Float2IntSortedMap)(this.compare(from, this.key) <= 0 ? this : Float2IntSortedMaps.EMPTY_MAP);
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
      public Float2IntSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2IntSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2IntSortedMap subMap(Float ofrom, Float oto) {
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

   public static class SynchronizedSortedMap extends Float2IntMaps.SynchronizedMap implements Float2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2IntSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2IntSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Float2IntSortedMap m) {
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
      public ObjectSortedSet<Float2IntMap.Entry> float2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Float2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Integer>> entrySet() {
         return this.float2IntEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2IntSortedMap subMap(float from, float to) {
         return new Float2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Float2IntSortedMap headMap(float to) {
         return new Float2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Float2IntSortedMap tailMap(float from) {
         return new Float2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Float2IntSortedMap subMap(Float from, Float to) {
         return new Float2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Float2IntSortedMap headMap(Float to) {
         return new Float2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Float2IntSortedMap tailMap(Float from) {
         return new Float2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Float2IntMaps.UnmodifiableMap implements Float2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2IntSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2IntSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Float2IntMap.Entry> float2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2IntEntrySet());
         }

         return (ObjectSortedSet<Float2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Integer>> entrySet() {
         return this.float2IntEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2IntSortedMap subMap(float from, float to) {
         return new Float2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Float2IntSortedMap headMap(float to) {
         return new Float2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Float2IntSortedMap tailMap(float from) {
         return new Float2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Float2IntSortedMap subMap(Float from, Float to) {
         return new Float2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Float2IntSortedMap headMap(Float to) {
         return new Float2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Float2IntSortedMap tailMap(Float from) {
         return new Float2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
