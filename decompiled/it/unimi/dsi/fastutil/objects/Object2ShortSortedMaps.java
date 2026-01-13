package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Object2ShortSortedMaps {
   public static final Object2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Object2ShortSortedMaps.EmptySortedMap();

   private Object2ShortSortedMaps() {
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
      return (x, y) -> comparator.compare(x.getKey(), y.getKey());
   }

   public static <K> ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> fastIterator(Object2ShortSortedMap<K> map) {
      ObjectSortedSet<Object2ShortMap.Entry<K>> entries = map.object2ShortEntrySet();
      return entries instanceof Object2ShortSortedMap.FastSortedEntrySet
         ? ((Object2ShortSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Object2ShortMap.Entry<K>> fastIterable(Object2ShortSortedMap<K> map) {
      ObjectSortedSet<Object2ShortMap.Entry<K>> entries = map.object2ShortEntrySet();
      return (ObjectBidirectionalIterable<Object2ShortMap.Entry<K>>)(entries instanceof Object2ShortSortedMap.FastSortedEntrySet
         ? ((Object2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <K> Object2ShortSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2ShortSortedMap<K> singleton(K key, Short value) {
      return new Object2ShortSortedMaps.Singleton<>(key, value);
   }

   public static <K> Object2ShortSortedMap<K> singleton(K key, Short value, Comparator<? super K> comparator) {
      return new Object2ShortSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Object2ShortSortedMap<K> singleton(K key, short value) {
      return new Object2ShortSortedMaps.Singleton<>(key, value);
   }

   public static <K> Object2ShortSortedMap<K> singleton(K key, short value, Comparator<? super K> comparator) {
      return new Object2ShortSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Object2ShortSortedMap<K> synchronize(Object2ShortSortedMap<K> m) {
      return new Object2ShortSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <K> Object2ShortSortedMap<K> synchronize(Object2ShortSortedMap<K> m, Object sync) {
      return new Object2ShortSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <K> Object2ShortSortedMap<K> unmodifiable(Object2ShortSortedMap<K> m) {
      return new Object2ShortSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<K> extends Object2ShortMaps.EmptyMap<K> implements Object2ShortSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Object2ShortMap.Entry<K>> object2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public Object2ShortSortedMap<K> subMap(K from, K to) {
         return Object2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Object2ShortSortedMap<K> headMap(K to) {
         return Object2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Object2ShortSortedMap<K> tailMap(K from) {
         return Object2ShortSortedMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Object2ShortMaps.Singleton<K> implements Object2ShortSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K key, short value, Comparator<? super K> comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(K key, short value) {
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
      public ObjectSortedSet<Object2ShortMap.Entry<K>> object2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractObject2ShortMap.BasicEntry<>(this.key, this.value), Object2ShortSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Object2ShortMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Short>> entrySet() {
         return this.object2ShortEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2ShortSortedMap<K> subMap(K from, K to) {
         return (Object2ShortSortedMap<K>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Object2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Object2ShortSortedMap<K> headMap(K to) {
         return (Object2ShortSortedMap<K>)(this.compare(this.key, to) < 0 ? this : Object2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Object2ShortSortedMap<K> tailMap(K from) {
         return (Object2ShortSortedMap<K>)(this.compare(from, this.key) <= 0 ? this : Object2ShortSortedMaps.EMPTY_MAP);
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

   public static class SynchronizedSortedMap<K> extends Object2ShortMaps.SynchronizedMap<K> implements Object2ShortSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ShortSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Object2ShortSortedMap<K> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Object2ShortSortedMap<K> m) {
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
      public ObjectSortedSet<Object2ShortMap.Entry<K>> object2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.object2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Object2ShortMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Short>> entrySet() {
         return this.object2ShortEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2ShortSortedMap<K> subMap(K from, K to) {
         return new Object2ShortSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Object2ShortSortedMap<K> headMap(K to) {
         return new Object2ShortSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Object2ShortSortedMap<K> tailMap(K from) {
         return new Object2ShortSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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

   public static class UnmodifiableSortedMap<K> extends Object2ShortMaps.UnmodifiableMap<K> implements Object2ShortSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ShortSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Object2ShortSortedMap<K> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Object2ShortMap.Entry<K>> object2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.object2ShortEntrySet());
         }

         return (ObjectSortedSet<Object2ShortMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Short>> entrySet() {
         return this.object2ShortEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2ShortSortedMap<K> subMap(K from, K to) {
         return new Object2ShortSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Object2ShortSortedMap<K> headMap(K to) {
         return new Object2ShortSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Object2ShortSortedMap<K> tailMap(K from) {
         return new Object2ShortSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
