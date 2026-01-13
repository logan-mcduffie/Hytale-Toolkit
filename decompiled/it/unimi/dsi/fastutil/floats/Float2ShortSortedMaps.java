package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Float2ShortSortedMaps {
   public static final Float2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Float2ShortSortedMaps.EmptySortedMap();

   private Float2ShortSortedMaps() {
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().floatValue(), y.getKey().floatValue());
   }

   public static ObjectBidirectionalIterator<Float2ShortMap.Entry> fastIterator(Float2ShortSortedMap map) {
      ObjectSortedSet<Float2ShortMap.Entry> entries = map.float2ShortEntrySet();
      return entries instanceof Float2ShortSortedMap.FastSortedEntrySet
         ? ((Float2ShortSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Float2ShortMap.Entry> fastIterable(Float2ShortSortedMap map) {
      ObjectSortedSet<Float2ShortMap.Entry> entries = map.float2ShortEntrySet();
      return (ObjectBidirectionalIterable<Float2ShortMap.Entry>)(entries instanceof Float2ShortSortedMap.FastSortedEntrySet
         ? ((Float2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Float2ShortSortedMap singleton(Float key, Short value) {
      return new Float2ShortSortedMaps.Singleton(key, value);
   }

   public static Float2ShortSortedMap singleton(Float key, Short value, FloatComparator comparator) {
      return new Float2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2ShortSortedMap singleton(float key, short value) {
      return new Float2ShortSortedMaps.Singleton(key, value);
   }

   public static Float2ShortSortedMap singleton(float key, short value, FloatComparator comparator) {
      return new Float2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2ShortSortedMap synchronize(Float2ShortSortedMap m) {
      return new Float2ShortSortedMaps.SynchronizedSortedMap(m);
   }

   public static Float2ShortSortedMap synchronize(Float2ShortSortedMap m, Object sync) {
      return new Float2ShortSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Float2ShortSortedMap unmodifiable(Float2ShortSortedMap m) {
      return new Float2ShortSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Float2ShortMaps.EmptyMap implements Float2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      @Override
      public Float2ShortSortedMap subMap(float from, float to) {
         return Float2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2ShortSortedMap headMap(float to) {
         return Float2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2ShortSortedMap tailMap(float from) {
         return Float2ShortSortedMaps.EMPTY_MAP;
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
      public Float2ShortSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2ShortSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2ShortSortedMap subMap(Float ofrom, Float oto) {
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

   public static class Singleton extends Float2ShortMaps.Singleton implements Float2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float key, short value, FloatComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(float key, short value) {
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
      public ObjectSortedSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractFloat2ShortMap.BasicEntry(this.key, this.value), Float2ShortSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Float2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Short>> entrySet() {
         return this.float2ShortEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ShortSortedMap subMap(float from, float to) {
         return (Float2ShortSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Float2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2ShortSortedMap headMap(float to) {
         return (Float2ShortSortedMap)(this.compare(this.key, to) < 0 ? this : Float2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2ShortSortedMap tailMap(float from) {
         return (Float2ShortSortedMap)(this.compare(from, this.key) <= 0 ? this : Float2ShortSortedMaps.EMPTY_MAP);
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
      public Float2ShortSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2ShortSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2ShortSortedMap subMap(Float ofrom, Float oto) {
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

   public static class SynchronizedSortedMap extends Float2ShortMaps.SynchronizedMap implements Float2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ShortSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2ShortSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Float2ShortSortedMap m) {
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
      public ObjectSortedSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Float2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Short>> entrySet() {
         return this.float2ShortEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ShortSortedMap subMap(float from, float to) {
         return new Float2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Float2ShortSortedMap headMap(float to) {
         return new Float2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Float2ShortSortedMap tailMap(float from) {
         return new Float2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Float2ShortSortedMap subMap(Float from, Float to) {
         return new Float2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Float2ShortSortedMap headMap(Float to) {
         return new Float2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Float2ShortSortedMap tailMap(Float from) {
         return new Float2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Float2ShortMaps.UnmodifiableMap implements Float2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ShortSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2ShortSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2ShortEntrySet());
         }

         return (ObjectSortedSet<Float2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Short>> entrySet() {
         return this.float2ShortEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ShortSortedMap subMap(float from, float to) {
         return new Float2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Float2ShortSortedMap headMap(float to) {
         return new Float2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Float2ShortSortedMap tailMap(float from) {
         return new Float2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Float2ShortSortedMap subMap(Float from, Float to) {
         return new Float2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Float2ShortSortedMap headMap(Float to) {
         return new Float2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Float2ShortSortedMap tailMap(Float from) {
         return new Float2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
