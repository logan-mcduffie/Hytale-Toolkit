package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Float2FloatSortedMaps {
   public static final Float2FloatSortedMaps.EmptySortedMap EMPTY_MAP = new Float2FloatSortedMaps.EmptySortedMap();

   private Float2FloatSortedMaps() {
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().floatValue(), y.getKey().floatValue());
   }

   public static ObjectBidirectionalIterator<Float2FloatMap.Entry> fastIterator(Float2FloatSortedMap map) {
      ObjectSortedSet<Float2FloatMap.Entry> entries = map.float2FloatEntrySet();
      return entries instanceof Float2FloatSortedMap.FastSortedEntrySet
         ? ((Float2FloatSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Float2FloatMap.Entry> fastIterable(Float2FloatSortedMap map) {
      ObjectSortedSet<Float2FloatMap.Entry> entries = map.float2FloatEntrySet();
      return (ObjectBidirectionalIterable<Float2FloatMap.Entry>)(entries instanceof Float2FloatSortedMap.FastSortedEntrySet
         ? ((Float2FloatSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Float2FloatSortedMap singleton(Float key, Float value) {
      return new Float2FloatSortedMaps.Singleton(key, value);
   }

   public static Float2FloatSortedMap singleton(Float key, Float value, FloatComparator comparator) {
      return new Float2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2FloatSortedMap singleton(float key, float value) {
      return new Float2FloatSortedMaps.Singleton(key, value);
   }

   public static Float2FloatSortedMap singleton(float key, float value, FloatComparator comparator) {
      return new Float2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2FloatSortedMap synchronize(Float2FloatSortedMap m) {
      return new Float2FloatSortedMaps.SynchronizedSortedMap(m);
   }

   public static Float2FloatSortedMap synchronize(Float2FloatSortedMap m, Object sync) {
      return new Float2FloatSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Float2FloatSortedMap unmodifiable(Float2FloatSortedMap m) {
      return new Float2FloatSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Float2FloatMaps.EmptyMap implements Float2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Float2FloatMap.Entry> float2FloatEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Float>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      @Override
      public Float2FloatSortedMap subMap(float from, float to) {
         return Float2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2FloatSortedMap headMap(float to) {
         return Float2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2FloatSortedMap tailMap(float from) {
         return Float2FloatSortedMaps.EMPTY_MAP;
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
      public Float2FloatSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2FloatSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2FloatSortedMap subMap(Float ofrom, Float oto) {
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

   public static class Singleton extends Float2FloatMaps.Singleton implements Float2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float key, float value, FloatComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(float key, float value) {
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
      public ObjectSortedSet<Float2FloatMap.Entry> float2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractFloat2FloatMap.BasicEntry(this.key, this.value), Float2FloatSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Float2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Float>> entrySet() {
         return this.float2FloatEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2FloatSortedMap subMap(float from, float to) {
         return (Float2FloatSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Float2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2FloatSortedMap headMap(float to) {
         return (Float2FloatSortedMap)(this.compare(this.key, to) < 0 ? this : Float2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2FloatSortedMap tailMap(float from) {
         return (Float2FloatSortedMap)(this.compare(from, this.key) <= 0 ? this : Float2FloatSortedMaps.EMPTY_MAP);
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
      public Float2FloatSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2FloatSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2FloatSortedMap subMap(Float ofrom, Float oto) {
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

   public static class SynchronizedSortedMap extends Float2FloatMaps.SynchronizedMap implements Float2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2FloatSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2FloatSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Float2FloatSortedMap m) {
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
      public ObjectSortedSet<Float2FloatMap.Entry> float2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2FloatEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Float2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Float>> entrySet() {
         return this.float2FloatEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2FloatSortedMap subMap(float from, float to) {
         return new Float2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Float2FloatSortedMap headMap(float to) {
         return new Float2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Float2FloatSortedMap tailMap(float from) {
         return new Float2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Float2FloatSortedMap subMap(Float from, Float to) {
         return new Float2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Float2FloatSortedMap headMap(Float to) {
         return new Float2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Float2FloatSortedMap tailMap(Float from) {
         return new Float2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Float2FloatMaps.UnmodifiableMap implements Float2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2FloatSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2FloatSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Float2FloatMap.Entry> float2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2FloatEntrySet());
         }

         return (ObjectSortedSet<Float2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Float>> entrySet() {
         return this.float2FloatEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2FloatSortedMap subMap(float from, float to) {
         return new Float2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Float2FloatSortedMap headMap(float to) {
         return new Float2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Float2FloatSortedMap tailMap(float from) {
         return new Float2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Float2FloatSortedMap subMap(Float from, Float to) {
         return new Float2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Float2FloatSortedMap headMap(Float to) {
         return new Float2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Float2FloatSortedMap tailMap(Float from) {
         return new Float2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
