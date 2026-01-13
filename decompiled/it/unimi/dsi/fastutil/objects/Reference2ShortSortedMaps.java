package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Reference2ShortSortedMaps {
   public static final Reference2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Reference2ShortSortedMaps.EmptySortedMap();

   private Reference2ShortSortedMaps() {
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
      return (x, y) -> comparator.compare(x.getKey(), y.getKey());
   }

   public static <K> ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> fastIterator(Reference2ShortSortedMap<K> map) {
      ObjectSortedSet<Reference2ShortMap.Entry<K>> entries = map.reference2ShortEntrySet();
      return entries instanceof Reference2ShortSortedMap.FastSortedEntrySet
         ? ((Reference2ShortSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Reference2ShortMap.Entry<K>> fastIterable(Reference2ShortSortedMap<K> map) {
      ObjectSortedSet<Reference2ShortMap.Entry<K>> entries = map.reference2ShortEntrySet();
      return (ObjectBidirectionalIterable<Reference2ShortMap.Entry<K>>)(entries instanceof Reference2ShortSortedMap.FastSortedEntrySet
         ? ((Reference2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <K> Reference2ShortSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2ShortSortedMap<K> singleton(K key, Short value) {
      return new Reference2ShortSortedMaps.Singleton<>(key, value);
   }

   public static <K> Reference2ShortSortedMap<K> singleton(K key, Short value, Comparator<? super K> comparator) {
      return new Reference2ShortSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Reference2ShortSortedMap<K> singleton(K key, short value) {
      return new Reference2ShortSortedMaps.Singleton<>(key, value);
   }

   public static <K> Reference2ShortSortedMap<K> singleton(K key, short value, Comparator<? super K> comparator) {
      return new Reference2ShortSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Reference2ShortSortedMap<K> synchronize(Reference2ShortSortedMap<K> m) {
      return new Reference2ShortSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <K> Reference2ShortSortedMap<K> synchronize(Reference2ShortSortedMap<K> m, Object sync) {
      return new Reference2ShortSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <K> Reference2ShortSortedMap<K> unmodifiable(Reference2ShortSortedMap<K> m) {
      return new Reference2ShortSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<K> extends Reference2ShortMaps.EmptyMap<K> implements Reference2ShortSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      @Override
      public Reference2ShortSortedMap<K> subMap(K from, K to) {
         return Reference2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Reference2ShortSortedMap<K> headMap(K to) {
         return Reference2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Reference2ShortSortedMap<K> tailMap(K from) {
         return Reference2ShortSortedMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Reference2ShortMaps.Singleton<K> implements Reference2ShortSortedMap<K>, Serializable, Cloneable {
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
      public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractReference2ShortMap.BasicEntry<>(this.key, this.value), Reference2ShortSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Reference2ShortMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Short>> entrySet() {
         return this.reference2ShortEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ShortSortedMap<K> subMap(K from, K to) {
         return (Reference2ShortSortedMap<K>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Reference2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Reference2ShortSortedMap<K> headMap(K to) {
         return (Reference2ShortSortedMap<K>)(this.compare(this.key, to) < 0 ? this : Reference2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Reference2ShortSortedMap<K> tailMap(K from) {
         return (Reference2ShortSortedMap<K>)(this.compare(from, this.key) <= 0 ? this : Reference2ShortSortedMaps.EMPTY_MAP);
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

   public static class SynchronizedSortedMap<K> extends Reference2ShortMaps.SynchronizedMap<K> implements Reference2ShortSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ShortSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Reference2ShortSortedMap<K> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Reference2ShortSortedMap<K> m) {
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
      public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Reference2ShortMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Short>> entrySet() {
         return this.reference2ShortEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ShortSortedMap<K> subMap(K from, K to) {
         return new Reference2ShortSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Reference2ShortSortedMap<K> headMap(K to) {
         return new Reference2ShortSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Reference2ShortSortedMap<K> tailMap(K from) {
         return new Reference2ShortSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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

   public static class UnmodifiableSortedMap<K> extends Reference2ShortMaps.UnmodifiableMap<K> implements Reference2ShortSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ShortSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Reference2ShortSortedMap<K> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2ShortEntrySet());
         }

         return (ObjectSortedSet<Reference2ShortMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Short>> entrySet() {
         return this.reference2ShortEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ShortSortedMap<K> subMap(K from, K to) {
         return new Reference2ShortSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Reference2ShortSortedMap<K> headMap(K to) {
         return new Reference2ShortSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Reference2ShortSortedMap<K> tailMap(K from) {
         return new Reference2ShortSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
