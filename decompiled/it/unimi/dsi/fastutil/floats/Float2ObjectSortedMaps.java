package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Float2ObjectSortedMaps {
   public static final Float2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Float2ObjectSortedMaps.EmptySortedMap();

   private Float2ObjectSortedMaps() {
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().floatValue(), y.getKey().floatValue());
   }

   public static <V> ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> fastIterator(Float2ObjectSortedMap<V> map) {
      ObjectSortedSet<Float2ObjectMap.Entry<V>> entries = map.float2ObjectEntrySet();
      return entries instanceof Float2ObjectSortedMap.FastSortedEntrySet
         ? ((Float2ObjectSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Float2ObjectMap.Entry<V>> fastIterable(Float2ObjectSortedMap<V> map) {
      ObjectSortedSet<Float2ObjectMap.Entry<V>> entries = map.float2ObjectEntrySet();
      return (ObjectBidirectionalIterable<Float2ObjectMap.Entry<V>>)(entries instanceof Float2ObjectSortedMap.FastSortedEntrySet
         ? ((Float2ObjectSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <V> Float2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Float2ObjectSortedMap<V> singleton(Float key, V value) {
      return new Float2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Float2ObjectSortedMap<V> singleton(Float key, V value, FloatComparator comparator) {
      return new Float2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Float2ObjectSortedMap<V> singleton(float key, V value) {
      return new Float2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Float2ObjectSortedMap<V> singleton(float key, V value, FloatComparator comparator) {
      return new Float2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Float2ObjectSortedMap<V> synchronize(Float2ObjectSortedMap<V> m) {
      return new Float2ObjectSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <V> Float2ObjectSortedMap<V> synchronize(Float2ObjectSortedMap<V> m, Object sync) {
      return new Float2ObjectSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <V> Float2ObjectSortedMap<V> unmodifiable(Float2ObjectSortedMap<? extends V> m) {
      return new Float2ObjectSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<V> extends Float2ObjectMaps.EmptyMap<V> implements Float2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      @Override
      public Float2ObjectSortedMap<V> subMap(float from, float to) {
         return Float2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2ObjectSortedMap<V> headMap(float to) {
         return Float2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2ObjectSortedMap<V> tailMap(float from) {
         return Float2ObjectSortedMaps.EMPTY_MAP;
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
      public Float2ObjectSortedMap<V> headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2ObjectSortedMap<V> tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2ObjectSortedMap<V> subMap(Float ofrom, Float oto) {
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

   public static class Singleton<V> extends Float2ObjectMaps.Singleton<V> implements Float2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float key, V value, FloatComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(float key, V value) {
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
      public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractFloat2ObjectMap.BasicEntry<>(this.key, this.value), Float2ObjectSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Float2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, V>> entrySet() {
         return this.float2ObjectEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ObjectSortedMap<V> subMap(float from, float to) {
         return (Float2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Float2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2ObjectSortedMap<V> headMap(float to) {
         return (Float2ObjectSortedMap<V>)(this.compare(this.key, to) < 0 ? this : Float2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2ObjectSortedMap<V> tailMap(float from) {
         return (Float2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 ? this : Float2ObjectSortedMaps.EMPTY_MAP);
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
      public Float2ObjectSortedMap<V> headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2ObjectSortedMap<V> tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2ObjectSortedMap<V> subMap(Float ofrom, Float oto) {
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

   public static class SynchronizedSortedMap<V> extends Float2ObjectMaps.SynchronizedMap<V> implements Float2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Float2ObjectSortedMap<V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Float2ObjectSortedMap<V> m) {
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
      public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Float2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, V>> entrySet() {
         return this.float2ObjectEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ObjectSortedMap<V> subMap(float from, float to) {
         return new Float2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Float2ObjectSortedMap<V> headMap(float to) {
         return new Float2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Float2ObjectSortedMap<V> tailMap(float from) {
         return new Float2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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
      public Float2ObjectSortedMap<V> subMap(Float from, Float to) {
         return new Float2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Float2ObjectSortedMap<V> headMap(Float to) {
         return new Float2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Float2ObjectSortedMap<V> tailMap(Float from) {
         return new Float2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap<V> extends Float2ObjectMaps.UnmodifiableMap<V> implements Float2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ObjectSortedMap<? extends V> sortedMap;

      protected UnmodifiableSortedMap(Float2ObjectSortedMap<? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Float2ObjectMap.Entry<V>>)this.sortedMap.float2ObjectEntrySet());
         }

         return (ObjectSortedSet<Float2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, V>> entrySet() {
         return this.float2ObjectEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ObjectSortedMap<V> subMap(float from, float to) {
         return new Float2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Float2ObjectSortedMap<V> headMap(float to) {
         return new Float2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Float2ObjectSortedMap<V> tailMap(float from) {
         return new Float2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
      public Float2ObjectSortedMap<V> subMap(Float from, Float to) {
         return new Float2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Float2ObjectSortedMap<V> headMap(Float to) {
         return new Float2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Float2ObjectSortedMap<V> tailMap(Float from) {
         return new Float2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }
   }
}
