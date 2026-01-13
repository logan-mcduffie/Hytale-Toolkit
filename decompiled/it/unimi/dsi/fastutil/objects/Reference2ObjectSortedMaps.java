package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Reference2ObjectSortedMaps {
   public static final Reference2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Reference2ObjectSortedMaps.EmptySortedMap();

   private Reference2ObjectSortedMaps() {
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
      return (x, y) -> comparator.compare(x.getKey(), y.getKey());
   }

   public static <K, V> ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> fastIterator(Reference2ObjectSortedMap<K, V> map) {
      ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> entries = map.reference2ObjectEntrySet();
      return entries instanceof Reference2ObjectSortedMap.FastSortedEntrySet
         ? ((Reference2ObjectSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <K, V> ObjectBidirectionalIterable<Reference2ObjectMap.Entry<K, V>> fastIterable(Reference2ObjectSortedMap<K, V> map) {
      ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> entries = map.reference2ObjectEntrySet();
      return (ObjectBidirectionalIterable<Reference2ObjectMap.Entry<K, V>>)(entries instanceof Reference2ObjectSortedMap.FastSortedEntrySet
         ? ((Reference2ObjectSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> singleton(K key, V value) {
      return new Reference2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> singleton(K key, V value, Comparator<? super K> comparator) {
      return new Reference2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> synchronize(Reference2ObjectSortedMap<K, V> m) {
      return new Reference2ObjectSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> synchronize(Reference2ObjectSortedMap<K, V> m, Object sync) {
      return new Reference2ObjectSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> unmodifiable(Reference2ObjectSortedMap<K, ? extends V> m) {
      return new Reference2ObjectSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<K, V> extends Reference2ObjectMaps.EmptyMap<K, V> implements Reference2ObjectSortedMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      @Override
      public Reference2ObjectSortedMap<K, V> subMap(K from, K to) {
         return Reference2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Reference2ObjectSortedMap<K, V> headMap(K to) {
         return Reference2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Reference2ObjectSortedMap<K, V> tailMap(K from) {
         return Reference2ObjectSortedMaps.EMPTY_MAP;
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

   public static class Singleton<K, V> extends Reference2ObjectMaps.Singleton<K, V> implements Reference2ObjectSortedMap<K, V>, Serializable, Cloneable {
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
      public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractReference2ObjectMap.BasicEntry<>(this.key, this.value),
               (Comparator<? super AbstractReference2ObjectMap.BasicEntry<K, V>>)Reference2ObjectSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Reference2ObjectMap.Entry<K, V>>)this.entries;
      }

      @Override
      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ObjectEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ObjectSortedMap<K, V> subMap(K from, K to) {
         return (Reference2ObjectSortedMap<K, V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0
            ? this
            : Reference2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Reference2ObjectSortedMap<K, V> headMap(K to) {
         return (Reference2ObjectSortedMap<K, V>)(this.compare(this.key, to) < 0 ? this : Reference2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Reference2ObjectSortedMap<K, V> tailMap(K from) {
         return (Reference2ObjectSortedMap<K, V>)(this.compare(from, this.key) <= 0 ? this : Reference2ObjectSortedMaps.EMPTY_MAP);
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

   public static class SynchronizedSortedMap<K, V> extends Reference2ObjectMaps.SynchronizedMap<K, V> implements Reference2ObjectSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ObjectSortedMap<K, V> sortedMap;

      protected SynchronizedSortedMap(Reference2ObjectSortedMap<K, V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Reference2ObjectSortedMap<K, V> m) {
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
      public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Reference2ObjectMap.Entry<K, V>>)this.entries;
      }

      @Override
      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ObjectEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ObjectSortedMap<K, V> subMap(K from, K to) {
         return new Reference2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Reference2ObjectSortedMap<K, V> headMap(K to) {
         return new Reference2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Reference2ObjectSortedMap<K, V> tailMap(K from) {
         return new Reference2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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

   public static class UnmodifiableSortedMap<K, V> extends Reference2ObjectMaps.UnmodifiableMap<K, V> implements Reference2ObjectSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ObjectSortedMap<K, ? extends V> sortedMap;

      protected UnmodifiableSortedMap(Reference2ObjectSortedMap<K, ? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Reference2ObjectMap.Entry<K, V>>)this.sortedMap.reference2ObjectEntrySet());
         }

         return (ObjectSortedSet<Reference2ObjectMap.Entry<K, V>>)this.entries;
      }

      @Override
      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ObjectEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ObjectSortedMap<K, V> subMap(K from, K to) {
         return new Reference2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Reference2ObjectSortedMap<K, V> headMap(K to) {
         return new Reference2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Reference2ObjectSortedMap<K, V> tailMap(K from) {
         return new Reference2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
