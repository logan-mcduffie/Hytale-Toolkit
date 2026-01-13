package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Object2LongSortedMaps {
   public static final Object2LongSortedMaps.EmptySortedMap EMPTY_MAP = new Object2LongSortedMaps.EmptySortedMap();

   private Object2LongSortedMaps() {
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
      return (x, y) -> comparator.compare(x.getKey(), y.getKey());
   }

   public static <K> ObjectBidirectionalIterator<Object2LongMap.Entry<K>> fastIterator(Object2LongSortedMap<K> map) {
      ObjectSortedSet<Object2LongMap.Entry<K>> entries = map.object2LongEntrySet();
      return entries instanceof Object2LongSortedMap.FastSortedEntrySet
         ? ((Object2LongSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Object2LongMap.Entry<K>> fastIterable(Object2LongSortedMap<K> map) {
      ObjectSortedSet<Object2LongMap.Entry<K>> entries = map.object2LongEntrySet();
      return (ObjectBidirectionalIterable<Object2LongMap.Entry<K>>)(entries instanceof Object2LongSortedMap.FastSortedEntrySet
         ? ((Object2LongSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <K> Object2LongSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2LongSortedMap<K> singleton(K key, Long value) {
      return new Object2LongSortedMaps.Singleton<>(key, value);
   }

   public static <K> Object2LongSortedMap<K> singleton(K key, Long value, Comparator<? super K> comparator) {
      return new Object2LongSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Object2LongSortedMap<K> singleton(K key, long value) {
      return new Object2LongSortedMaps.Singleton<>(key, value);
   }

   public static <K> Object2LongSortedMap<K> singleton(K key, long value, Comparator<? super K> comparator) {
      return new Object2LongSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Object2LongSortedMap<K> synchronize(Object2LongSortedMap<K> m) {
      return new Object2LongSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <K> Object2LongSortedMap<K> synchronize(Object2LongSortedMap<K> m, Object sync) {
      return new Object2LongSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <K> Object2LongSortedMap<K> unmodifiable(Object2LongSortedMap<K> m) {
      return new Object2LongSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<K> extends Object2LongMaps.EmptyMap<K> implements Object2LongSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Long>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public Object2LongSortedMap<K> subMap(K from, K to) {
         return Object2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public Object2LongSortedMap<K> headMap(K to) {
         return Object2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public Object2LongSortedMap<K> tailMap(K from) {
         return Object2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public K firstKey() {
         throw new NoSuchElementException();
      }

      @Override
      public K lastKey() {
         throw new NoSuchElementException();
      }
   }

   public static class Singleton<K> extends Object2LongMaps.Singleton<K> implements Object2LongSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K key, long value, Comparator<? super K> comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(K key, long value) {
         this(key, value, null);
      }

      final int compare(K k1, K k2) {
         return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSortedSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractObject2LongMap.BasicEntry<>(this.key, this.value), Object2LongSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Object2LongMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2LongSortedMap<K> subMap(K from, K to) {
         return (Object2LongSortedMap<K>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Object2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public Object2LongSortedMap<K> headMap(K to) {
         return (Object2LongSortedMap<K>)(this.compare(this.key, to) < 0 ? this : Object2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public Object2LongSortedMap<K> tailMap(K from) {
         return (Object2LongSortedMap<K>)(this.compare(from, this.key) <= 0 ? this : Object2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public K firstKey() {
         return this.key;
      }

      @Override
      public K lastKey() {
         return this.key;
      }
   }

   public static class SynchronizedSortedMap<K> extends Object2LongMaps.SynchronizedMap<K> implements Object2LongSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2LongSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Object2LongSortedMap<K> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Object2LongSortedMap<K> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public Comparator<? super K> comparator() {
         synchronized (this.sync) {
            return this.sortedMap.comparator();
         }
      }

      @Override
      public ObjectSortedSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.object2LongEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Object2LongMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2LongSortedMap<K> subMap(K from, K to) {
         return new Object2LongSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Object2LongSortedMap<K> headMap(K to) {
         return new Object2LongSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Object2LongSortedMap<K> tailMap(K from) {
         return new Object2LongSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }

      @Override
      public K firstKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      @Override
      public K lastKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastKey();
         }
      }
   }

   public static class UnmodifiableSortedMap<K> extends Object2LongMaps.UnmodifiableMap<K> implements Object2LongSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2LongSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Object2LongSortedMap<K> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.object2LongEntrySet());
         }

         return (ObjectSortedSet<Object2LongMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2LongSortedMap<K> subMap(K from, K to) {
         return new Object2LongSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Object2LongSortedMap<K> headMap(K to) {
         return new Object2LongSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Object2LongSortedMap<K> tailMap(K from) {
         return new Object2LongSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }

      @Override
      public K firstKey() {
         return this.sortedMap.firstKey();
      }

      @Override
      public K lastKey() {
         return this.sortedMap.lastKey();
      }
   }
}
