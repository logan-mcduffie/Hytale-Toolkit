package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Object2ReferenceSortedMaps {
   public static final Object2ReferenceSortedMaps.EmptySortedMap EMPTY_MAP = new Object2ReferenceSortedMaps.EmptySortedMap();

   private Object2ReferenceSortedMaps() {
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
      return (x, y) -> comparator.compare(x.getKey(), y.getKey());
   }

   public static <K, V> ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> fastIterator(Object2ReferenceSortedMap<K, V> map) {
      ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> entries = map.object2ReferenceEntrySet();
      return entries instanceof Object2ReferenceSortedMap.FastSortedEntrySet
         ? ((Object2ReferenceSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <K, V> ObjectBidirectionalIterable<Object2ReferenceMap.Entry<K, V>> fastIterable(Object2ReferenceSortedMap<K, V> map) {
      ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> entries = map.object2ReferenceEntrySet();
      return (ObjectBidirectionalIterable<Object2ReferenceMap.Entry<K, V>>)(entries instanceof Object2ReferenceSortedMap.FastSortedEntrySet
         ? ((Object2ReferenceSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <K, V> Object2ReferenceSortedMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Object2ReferenceSortedMap<K, V> singleton(K key, V value) {
      return new Object2ReferenceSortedMaps.Singleton<>(key, value);
   }

   public static <K, V> Object2ReferenceSortedMap<K, V> singleton(K key, V value, Comparator<? super K> comparator) {
      return new Object2ReferenceSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K, V> Object2ReferenceSortedMap<K, V> synchronize(Object2ReferenceSortedMap<K, V> m) {
      return new Object2ReferenceSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <K, V> Object2ReferenceSortedMap<K, V> synchronize(Object2ReferenceSortedMap<K, V> m, Object sync) {
      return new Object2ReferenceSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <K, V> Object2ReferenceSortedMap<K, V> unmodifiable(Object2ReferenceSortedMap<K, ? extends V> m) {
      return new Object2ReferenceSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<K, V> extends Object2ReferenceMaps.EmptyMap<K, V> implements Object2ReferenceSortedMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
         return Object2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public Object2ReferenceSortedMap<K, V> headMap(K to) {
         return Object2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public Object2ReferenceSortedMap<K, V> tailMap(K from) {
         return Object2ReferenceSortedMaps.EMPTY_MAP;
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

   public static class Singleton<K, V> extends Object2ReferenceMaps.Singleton<K, V> implements Object2ReferenceSortedMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K key, V value, Comparator<? super K> comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(K key, V value) {
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
      public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractObject2ReferenceMap.BasicEntry<>(this.key, this.value),
               (Comparator<? super AbstractObject2ReferenceMap.BasicEntry<K, V>>)Object2ReferenceSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Object2ReferenceMap.Entry<K, V>>)this.entries;
      }

      @Override
      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.object2ReferenceEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
         return (Object2ReferenceSortedMap<K, V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0
            ? this
            : Object2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public Object2ReferenceSortedMap<K, V> headMap(K to) {
         return (Object2ReferenceSortedMap<K, V>)(this.compare(this.key, to) < 0 ? this : Object2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public Object2ReferenceSortedMap<K, V> tailMap(K from) {
         return (Object2ReferenceSortedMap<K, V>)(this.compare(from, this.key) <= 0 ? this : Object2ReferenceSortedMaps.EMPTY_MAP);
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

   public static class SynchronizedSortedMap<K, V> extends Object2ReferenceMaps.SynchronizedMap<K, V> implements Object2ReferenceSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ReferenceSortedMap<K, V> sortedMap;

      protected SynchronizedSortedMap(Object2ReferenceSortedMap<K, V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Object2ReferenceSortedMap<K, V> m) {
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
      public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.object2ReferenceEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Object2ReferenceMap.Entry<K, V>>)this.entries;
      }

      @Override
      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.object2ReferenceEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
         return new Object2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Object2ReferenceSortedMap<K, V> headMap(K to) {
         return new Object2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Object2ReferenceSortedMap<K, V> tailMap(K from) {
         return new Object2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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

   public static class UnmodifiableSortedMap<K, V> extends Object2ReferenceMaps.UnmodifiableMap<K, V> implements Object2ReferenceSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ReferenceSortedMap<K, ? extends V> sortedMap;

      protected UnmodifiableSortedMap(Object2ReferenceSortedMap<K, ? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Object2ReferenceMap.Entry<K, V>>)this.sortedMap.object2ReferenceEntrySet());
         }

         return (ObjectSortedSet<Object2ReferenceMap.Entry<K, V>>)this.entries;
      }

      @Override
      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.object2ReferenceEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
         return new Object2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Object2ReferenceSortedMap<K, V> headMap(K to) {
         return new Object2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Object2ReferenceSortedMap<K, V> tailMap(K from) {
         return new Object2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
