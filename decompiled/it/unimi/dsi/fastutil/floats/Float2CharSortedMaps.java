package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Float2CharSortedMaps {
   public static final Float2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Float2CharSortedMaps.EmptySortedMap();

   private Float2CharSortedMaps() {
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().floatValue(), y.getKey().floatValue());
   }

   public static ObjectBidirectionalIterator<Float2CharMap.Entry> fastIterator(Float2CharSortedMap map) {
      ObjectSortedSet<Float2CharMap.Entry> entries = map.float2CharEntrySet();
      return entries instanceof Float2CharSortedMap.FastSortedEntrySet ? ((Float2CharSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Float2CharMap.Entry> fastIterable(Float2CharSortedMap map) {
      ObjectSortedSet<Float2CharMap.Entry> entries = map.float2CharEntrySet();
      return (ObjectBidirectionalIterable<Float2CharMap.Entry>)(entries instanceof Float2CharSortedMap.FastSortedEntrySet
         ? ((Float2CharSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Float2CharSortedMap singleton(Float key, Character value) {
      return new Float2CharSortedMaps.Singleton(key, value);
   }

   public static Float2CharSortedMap singleton(Float key, Character value, FloatComparator comparator) {
      return new Float2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2CharSortedMap singleton(float key, char value) {
      return new Float2CharSortedMaps.Singleton(key, value);
   }

   public static Float2CharSortedMap singleton(float key, char value, FloatComparator comparator) {
      return new Float2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Float2CharSortedMap synchronize(Float2CharSortedMap m) {
      return new Float2CharSortedMaps.SynchronizedSortedMap(m);
   }

   public static Float2CharSortedMap synchronize(Float2CharSortedMap m, Object sync) {
      return new Float2CharSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Float2CharSortedMap unmodifiable(Float2CharSortedMap m) {
      return new Float2CharSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Float2CharMaps.EmptyMap implements Float2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      @Override
      public Float2CharSortedMap subMap(float from, float to) {
         return Float2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2CharSortedMap headMap(float to) {
         return Float2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Float2CharSortedMap tailMap(float from) {
         return Float2CharSortedMaps.EMPTY_MAP;
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
      public Float2CharSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2CharSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2CharSortedMap subMap(Float ofrom, Float oto) {
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

   public static class Singleton extends Float2CharMaps.Singleton implements Float2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float key, char value, FloatComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(float key, char value) {
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
      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractFloat2CharMap.BasicEntry(this.key, this.value), Float2CharSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Float2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2CharSortedMap subMap(float from, float to) {
         return (Float2CharSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Float2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2CharSortedMap headMap(float to) {
         return (Float2CharSortedMap)(this.compare(this.key, to) < 0 ? this : Float2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Float2CharSortedMap tailMap(float from) {
         return (Float2CharSortedMap)(this.compare(from, this.key) <= 0 ? this : Float2CharSortedMaps.EMPTY_MAP);
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
      public Float2CharSortedMap headMap(Float oto) {
         return this.headMap(oto.floatValue());
      }

      @Deprecated
      @Override
      public Float2CharSortedMap tailMap(Float ofrom) {
         return this.tailMap(ofrom.floatValue());
      }

      @Deprecated
      @Override
      public Float2CharSortedMap subMap(Float ofrom, Float oto) {
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

   public static class SynchronizedSortedMap extends Float2CharMaps.SynchronizedMap implements Float2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2CharSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Float2CharSortedMap m) {
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
      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Float2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2CharSortedMap subMap(float from, float to) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Float2CharSortedMap headMap(float to) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Float2CharSortedMap tailMap(float from) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Float2CharSortedMap subMap(Float from, Float to) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Float2CharSortedMap headMap(Float to) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Float2CharSortedMap tailMap(Float from) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Float2CharMaps.UnmodifiableMap implements Float2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2CharSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2CharEntrySet());
         }

         return (ObjectSortedSet<Float2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      @Override
      public Float2CharSortedMap subMap(float from, float to) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Float2CharSortedMap headMap(float to) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Float2CharSortedMap tailMap(float from) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Float2CharSortedMap subMap(Float from, Float to) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Float2CharSortedMap headMap(Float to) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Float2CharSortedMap tailMap(Float from) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
