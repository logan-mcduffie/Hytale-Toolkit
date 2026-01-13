package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Object2CharSortedMaps {
   public static final Object2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Object2CharSortedMaps.EmptySortedMap();

   private Object2CharSortedMaps() {
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
      return (x, y) -> comparator.compare(x.getKey(), y.getKey());
   }

   public static <K> ObjectBidirectionalIterator<Object2CharMap.Entry<K>> fastIterator(Object2CharSortedMap<K> map) {
      ObjectSortedSet<Object2CharMap.Entry<K>> entries = map.object2CharEntrySet();
      return entries instanceof Object2CharSortedMap.FastSortedEntrySet
         ? ((Object2CharSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Object2CharMap.Entry<K>> fastIterable(Object2CharSortedMap<K> map) {
      ObjectSortedSet<Object2CharMap.Entry<K>> entries = map.object2CharEntrySet();
      return (ObjectBidirectionalIterable<Object2CharMap.Entry<K>>)(entries instanceof Object2CharSortedMap.FastSortedEntrySet
         ? ((Object2CharSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <K> Object2CharSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2CharSortedMap<K> singleton(K key, Character value) {
      return new Object2CharSortedMaps.Singleton<>(key, value);
   }

   public static <K> Object2CharSortedMap<K> singleton(K key, Character value, Comparator<? super K> comparator) {
      return new Object2CharSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Object2CharSortedMap<K> singleton(K key, char value) {
      return new Object2CharSortedMaps.Singleton<>(key, value);
   }

   public static <K> Object2CharSortedMap<K> singleton(K key, char value, Comparator<? super K> comparator) {
      return new Object2CharSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Object2CharSortedMap<K> synchronize(Object2CharSortedMap<K> m) {
      return new Object2CharSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <K> Object2CharSortedMap<K> synchronize(Object2CharSortedMap<K> m, Object sync) {
      return new Object2CharSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <K> Object2CharSortedMap<K> unmodifiable(Object2CharSortedMap<K> m) {
      return new Object2CharSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<K> extends Object2CharMaps.EmptyMap<K> implements Object2CharSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public Object2CharSortedMap<K> subMap(K from, K to) {
         return Object2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Object2CharSortedMap<K> headMap(K to) {
         return Object2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Object2CharSortedMap<K> tailMap(K from) {
         return Object2CharSortedMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Object2CharMaps.Singleton<K> implements Object2CharSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K key, char value, Comparator<? super K> comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(K key, char value) {
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
      public ObjectSortedSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractObject2CharMap.BasicEntry<>(this.key, this.value), Object2CharSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Object2CharMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Character>> entrySet() {
         return this.object2CharEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2CharSortedMap<K> subMap(K from, K to) {
         return (Object2CharSortedMap<K>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Object2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Object2CharSortedMap<K> headMap(K to) {
         return (Object2CharSortedMap<K>)(this.compare(this.key, to) < 0 ? this : Object2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Object2CharSortedMap<K> tailMap(K from) {
         return (Object2CharSortedMap<K>)(this.compare(from, this.key) <= 0 ? this : Object2CharSortedMaps.EMPTY_MAP);
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

   public static class SynchronizedSortedMap<K> extends Object2CharMaps.SynchronizedMap<K> implements Object2CharSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2CharSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Object2CharSortedMap<K> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Object2CharSortedMap<K> m) {
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
      public ObjectSortedSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.object2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Object2CharMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Character>> entrySet() {
         return this.object2CharEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2CharSortedMap<K> subMap(K from, K to) {
         return new Object2CharSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Object2CharSortedMap<K> headMap(K to) {
         return new Object2CharSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Object2CharSortedMap<K> tailMap(K from) {
         return new Object2CharSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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

   public static class UnmodifiableSortedMap<K> extends Object2CharMaps.UnmodifiableMap<K> implements Object2CharSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2CharSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Object2CharSortedMap<K> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.object2CharEntrySet());
         }

         return (ObjectSortedSet<Object2CharMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Character>> entrySet() {
         return this.object2CharEntrySet();
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ObjectSortedSet<K>)this.keys;
      }

      @Override
      public Object2CharSortedMap<K> subMap(K from, K to) {
         return new Object2CharSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Object2CharSortedMap<K> headMap(K to) {
         return new Object2CharSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Object2CharSortedMap<K> tailMap(K from) {
         return new Object2CharSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
