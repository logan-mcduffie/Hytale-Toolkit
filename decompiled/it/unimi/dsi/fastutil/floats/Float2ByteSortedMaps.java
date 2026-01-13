package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Float2ByteSortedMaps {
   public static final Float2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Float2ByteSortedMaps.EmptySortedMap();

   private Float2ByteSortedMaps() {
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().floatValue(), y.getKey().floatValue());
   }

   public static ObjectBidirectionalIterator<Float2ByteMap.Entry> fastIterator(Float2ByteSortedMap map) {
      ObjectSortedSet<Float2ByteMap.Entry> entries = map.float2ByteEntrySet();
      return entries instanceof Float2ByteSortedMap.FastSortedEntrySet ? ((Float2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Float2ByteMap.Entry> fastIterable(Float2ByteSortedMap map) {
      ObjectSortedSet<Float2ByteMap.Entry> entries = map.float2ByteEntrySet();
      return (ObjectBidirectionalIterable<Float2ByteMap.Entry>)(entries instanceof Float2ByteSortedMap.FastSortedEntrySet
         ? ((Float2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Float2ByteSortedMap singleton(Float key, Byte value) {
      return new Float2ByteSortedMaps.Singleton(key, value);
   }

   public static Float2ByteSortedMap singleton(Float key, Byte value, FloatComparator comparator) {
      return new Float2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2ByteSortedMap singleton(float key, byte value) {
      return new Float2ByteSortedMaps.Singleton(key, value);
   }

   public static Float2ByteSortedMap singleton(float key, byte value, FloatComparator comparator) {
      return new Float2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2ByteSortedMap synchronize(Float2ByteSortedMap m) {
      return new Float2ByteSortedMaps.SynchronizedSortedMap(m);
   }

   public static Float2ByteSortedMap synchronize(Float2ByteSortedMap m, Object sync) {
      return new Float2ByteSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Float2ByteSortedMap unmodifiable(Float2ByteSortedMap m) {
      return new Float2ByteSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Float2ByteMaps.EmptyMap implements Float2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      @Override
      public Float2ByteSortedMap subMap(float from, float to) {
         return Float2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2ByteSortedMap headMap(float to) {
         return Float2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2ByteSortedMap tailMap(float from) {
         return Float2ByteSortedMaps.EMPTY_MAP;
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
      public Float2ByteSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2ByteSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2ByteSortedMap subMap(Float ofrom, Float oto) {
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

   public static class Singleton extends Float2ByteMaps.Singleton implements Float2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float key, byte value, FloatComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(float key, byte value) {
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
      public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractFloat2ByteMap.BasicEntry(this.key, this.value), Float2ByteSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Float2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ByteSortedMap subMap(float from, float to) {
         return (Float2ByteSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Float2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2ByteSortedMap headMap(float to) {
         return (Float2ByteSortedMap)(this.compare(this.key, to) < 0 ? this : Float2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2ByteSortedMap tailMap(float from) {
         return (Float2ByteSortedMap)(this.compare(from, this.key) <= 0 ? this : Float2ByteSortedMaps.EMPTY_MAP);
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
      public Float2ByteSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2ByteSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2ByteSortedMap subMap(Float ofrom, Float oto) {
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

   public static class SynchronizedSortedMap extends Float2ByteMaps.SynchronizedMap implements Float2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ByteSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2ByteSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Float2ByteSortedMap m) {
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
      public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Float2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ByteSortedMap subMap(float from, float to) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Float2ByteSortedMap headMap(float to) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Float2ByteSortedMap tailMap(float from) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Float2ByteSortedMap subMap(Float from, Float to) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Float2ByteSortedMap headMap(Float to) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Float2ByteSortedMap tailMap(Float from) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Float2ByteMaps.UnmodifiableMap implements Float2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ByteSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2ByteSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2ByteEntrySet());
         }

         return (ObjectSortedSet<Float2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2ByteSortedMap subMap(float from, float to) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Float2ByteSortedMap headMap(float to) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Float2ByteSortedMap tailMap(float from) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Float2ByteSortedMap subMap(Float from, Float to) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Float2ByteSortedMap headMap(Float to) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Float2ByteSortedMap tailMap(Float from) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
