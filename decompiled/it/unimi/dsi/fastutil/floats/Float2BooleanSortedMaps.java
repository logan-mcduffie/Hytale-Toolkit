package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Float2BooleanSortedMaps {
   public static final Float2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Float2BooleanSortedMaps.EmptySortedMap();

   private Float2BooleanSortedMaps() {
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().floatValue(), y.getKey().floatValue());
   }

   public static ObjectBidirectionalIterator<Float2BooleanMap.Entry> fastIterator(Float2BooleanSortedMap map) {
      ObjectSortedSet<Float2BooleanMap.Entry> entries = map.float2BooleanEntrySet();
      return entries instanceof Float2BooleanSortedMap.FastSortedEntrySet
         ? ((Float2BooleanSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Float2BooleanMap.Entry> fastIterable(Float2BooleanSortedMap map) {
      ObjectSortedSet<Float2BooleanMap.Entry> entries = map.float2BooleanEntrySet();
      return (ObjectBidirectionalIterable<Float2BooleanMap.Entry>)(entries instanceof Float2BooleanSortedMap.FastSortedEntrySet
         ? ((Float2BooleanSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Float2BooleanSortedMap singleton(Float key, Boolean value) {
      return new Float2BooleanSortedMaps.Singleton(key, value);
   }

   public static Float2BooleanSortedMap singleton(Float key, Boolean value, FloatComparator comparator) {
      return new Float2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2BooleanSortedMap singleton(float key, boolean value) {
      return new Float2BooleanSortedMaps.Singleton(key, value);
   }

   public static Float2BooleanSortedMap singleton(float key, boolean value, FloatComparator comparator) {
      return new Float2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2BooleanSortedMap synchronize(Float2BooleanSortedMap m) {
      return new Float2BooleanSortedMaps.SynchronizedSortedMap(m);
   }

   public static Float2BooleanSortedMap synchronize(Float2BooleanSortedMap m, Object sync) {
      return new Float2BooleanSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Float2BooleanSortedMap unmodifiable(Float2BooleanSortedMap m) {
      return new Float2BooleanSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Float2BooleanMaps.EmptyMap implements Float2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Float2BooleanMap.Entry> float2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      @Override
      public Float2BooleanSortedMap subMap(float from, float to) {
         return Float2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2BooleanSortedMap headMap(float to) {
         return Float2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2BooleanSortedMap tailMap(float from) {
         return Float2BooleanSortedMaps.EMPTY_MAP;
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
      public Float2BooleanSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2BooleanSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2BooleanSortedMap subMap(Float ofrom, Float oto) {
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

   public static class Singleton extends Float2BooleanMaps.Singleton implements Float2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float key, boolean value, FloatComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(float key, boolean value) {
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
      public ObjectSortedSet<Float2BooleanMap.Entry> float2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractFloat2BooleanMap.BasicEntry(this.key, this.value), Float2BooleanSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Float2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Boolean>> entrySet() {
         return this.float2BooleanEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2BooleanSortedMap subMap(float from, float to) {
         return (Float2BooleanSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Float2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2BooleanSortedMap headMap(float to) {
         return (Float2BooleanSortedMap)(this.compare(this.key, to) < 0 ? this : Float2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2BooleanSortedMap tailMap(float from) {
         return (Float2BooleanSortedMap)(this.compare(from, this.key) <= 0 ? this : Float2BooleanSortedMaps.EMPTY_MAP);
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
      public Float2BooleanSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2BooleanSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2BooleanSortedMap subMap(Float ofrom, Float oto) {
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

   public static class SynchronizedSortedMap extends Float2BooleanMaps.SynchronizedMap implements Float2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2BooleanSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Float2BooleanSortedMap m) {
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
      public ObjectSortedSet<Float2BooleanMap.Entry> float2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Float2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Boolean>> entrySet() {
         return this.float2BooleanEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2BooleanSortedMap subMap(float from, float to) {
         return new Float2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Float2BooleanSortedMap headMap(float to) {
         return new Float2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Float2BooleanSortedMap tailMap(float from) {
         return new Float2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Float2BooleanSortedMap subMap(Float from, Float to) {
         return new Float2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Float2BooleanSortedMap headMap(Float to) {
         return new Float2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Float2BooleanSortedMap tailMap(Float from) {
         return new Float2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Float2BooleanMaps.UnmodifiableMap implements Float2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2BooleanSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Float2BooleanMap.Entry> float2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2BooleanEntrySet());
         }

         return (ObjectSortedSet<Float2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Boolean>> entrySet() {
         return this.float2BooleanEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2BooleanSortedMap subMap(float from, float to) {
         return new Float2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Float2BooleanSortedMap headMap(float to) {
         return new Float2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Float2BooleanSortedMap tailMap(float from) {
         return new Float2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Float2BooleanSortedMap subMap(Float from, Float to) {
         return new Float2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Float2BooleanSortedMap headMap(Float to) {
         return new Float2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Float2BooleanSortedMap tailMap(Float from) {
         return new Float2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
