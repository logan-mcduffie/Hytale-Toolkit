package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Reference2ByteSortedMaps {
   public static final Reference2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Reference2ByteSortedMaps.EmptySortedMap();

   private Reference2ByteSortedMaps() {
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
      return (x, y) -> comparator.compare(x.getKey(), y.getKey());
   }

   public static <K> ObjectBidirectionalIterator<Reference2ByteMap.Entry<K>> fastIterator(Reference2ByteSortedMap<K> map) {
      ObjectSortedSet<Reference2ByteMap.Entry<K>> entries = map.reference2ByteEntrySet();
      return entries instanceof Reference2ByteSortedMap.FastSortedEntrySet
         ? ((Reference2ByteSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Reference2ByteMap.Entry<K>> fastIterable(Reference2ByteSortedMap<K> map) {
      ObjectSortedSet<Reference2ByteMap.Entry<K>> entries = map.reference2ByteEntrySet();
      return (ObjectBidirectionalIterable<Reference2ByteMap.Entry<K>>)(entries instanceof Reference2ByteSortedMap.FastSortedEntrySet
         ? ((Reference2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <K> Reference2ByteSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2ByteSortedMap<K> singleton(K key, Byte value) {
      return new Reference2ByteSortedMaps.Singleton<>(key, value);
   }

   public static <K> Reference2ByteSortedMap<K> singleton(K key, Byte value, Comparator<? super K> comparator) {
      return new Reference2ByteSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Reference2ByteSortedMap<K> singleton(K key, byte value) {
      return new Reference2ByteSortedMaps.Singleton<>(key, value);
   }

   public static <K> Reference2ByteSortedMap<K> singleton(K key, byte value, Comparator<? super K> comparator) {
      return new Reference2ByteSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <K> Reference2ByteSortedMap<K> synchronize(Reference2ByteSortedMap<K> m) {
      return new Reference2ByteSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <K> Reference2ByteSortedMap<K> synchronize(Reference2ByteSortedMap<K> m, Object sync) {
      return new Reference2ByteSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <K> Reference2ByteSortedMap<K> unmodifiable(Reference2ByteSortedMap<K> m) {
      return new Reference2ByteSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<K> extends Reference2ByteMaps.EmptyMap<K> implements Reference2ByteSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      @Override
      public Reference2ByteSortedMap<K> subMap(K from, K to) {
         return Reference2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Reference2ByteSortedMap<K> headMap(K to) {
         return Reference2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Reference2ByteSortedMap<K> tailMap(K from) {
         return Reference2ByteSortedMaps.EMPTY_MAP;
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

   public static class Singleton<K> extends Reference2ByteMaps.Singleton<K> implements Reference2ByteSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K key, byte value, Comparator<? super K> comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(K key, byte value) {
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
      public ObjectSortedSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractReference2ByteMap.BasicEntry<>(this.key, this.value), Reference2ByteSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Reference2ByteMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Byte>> entrySet() {
         return this.reference2ByteEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ByteSortedMap<K> subMap(K from, K to) {
         return (Reference2ByteSortedMap<K>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Reference2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Reference2ByteSortedMap<K> headMap(K to) {
         return (Reference2ByteSortedMap<K>)(this.compare(this.key, to) < 0 ? this : Reference2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Reference2ByteSortedMap<K> tailMap(K from) {
         return (Reference2ByteSortedMap<K>)(this.compare(from, this.key) <= 0 ? this : Reference2ByteSortedMaps.EMPTY_MAP);
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

   public static class SynchronizedSortedMap<K> extends Reference2ByteMaps.SynchronizedMap<K> implements Reference2ByteSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ByteSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Reference2ByteSortedMap<K> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Reference2ByteSortedMap<K> m) {
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
      public ObjectSortedSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Reference2ByteMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Byte>> entrySet() {
         return this.reference2ByteEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ByteSortedMap<K> subMap(K from, K to) {
         return new Reference2ByteSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Reference2ByteSortedMap<K> headMap(K to) {
         return new Reference2ByteSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Reference2ByteSortedMap<K> tailMap(K from) {
         return new Reference2ByteSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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

   public static class UnmodifiableSortedMap<K> extends Reference2ByteMaps.UnmodifiableMap<K> implements Reference2ByteSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ByteSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Reference2ByteSortedMap<K> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2ByteEntrySet());
         }

         return (ObjectSortedSet<Reference2ByteMap.Entry<K>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<K, Byte>> entrySet() {
         return this.reference2ByteEntrySet();
      }

      @Override
      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ReferenceSortedSet<K>)this.keys;
      }

      @Override
      public Reference2ByteSortedMap<K> subMap(K from, K to) {
         return new Reference2ByteSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Reference2ByteSortedMap<K> headMap(K to) {
         return new Reference2ByteSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Reference2ByteSortedMap<K> tailMap(K from) {
         return new Reference2ByteSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
