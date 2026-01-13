package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Long2IntSortedMaps {
   public static final Long2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Long2IntSortedMaps.EmptySortedMap();

   private Long2IntSortedMaps() {
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().longValue(), y.getKey().longValue());
   }

   public static ObjectBidirectionalIterator<Long2IntMap.Entry> fastIterator(Long2IntSortedMap map) {
      ObjectSortedSet<Long2IntMap.Entry> entries = map.long2IntEntrySet();
      return entries instanceof Long2IntSortedMap.FastSortedEntrySet ? ((Long2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Long2IntMap.Entry> fastIterable(Long2IntSortedMap map) {
      ObjectSortedSet<Long2IntMap.Entry> entries = map.long2IntEntrySet();
      return (ObjectBidirectionalIterable<Long2IntMap.Entry>)(entries instanceof Long2IntSortedMap.FastSortedEntrySet
         ? ((Long2IntSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Long2IntSortedMap singleton(Long key, Integer value) {
      return new Long2IntSortedMaps.Singleton(key, value);
   }

   public static Long2IntSortedMap singleton(Long key, Integer value, LongComparator comparator) {
      return new Long2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2IntSortedMap singleton(long key, int value) {
      return new Long2IntSortedMaps.Singleton(key, value);
   }

   public static Long2IntSortedMap singleton(long key, int value, LongComparator comparator) {
      return new Long2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2IntSortedMap synchronize(Long2IntSortedMap m) {
      return new Long2IntSortedMaps.SynchronizedSortedMap(m);
   }

   public static Long2IntSortedMap synchronize(Long2IntSortedMap m, Object sync) {
      return new Long2IntSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Long2IntSortedMap unmodifiable(Long2IntSortedMap m) {
      return new Long2IntSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Long2IntMaps.EmptyMap implements Long2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      @Override
      public Long2IntSortedMap subMap(long from, long to) {
         return Long2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2IntSortedMap headMap(long to) {
         return Long2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2IntSortedMap tailMap(long from) {
         return Long2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public long firstLongKey() {
         throw new NoSuchElementException();
      }

      @Override
      public long lastLongKey() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Long2IntSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2IntSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2IntSortedMap subMap(Long ofrom, Long oto) {
         return this.subMap(ofrom.longValue(), oto.longValue());
      }

      @Deprecated
      @Override
      public Long firstKey() {
         return this.firstLongKey();
      }

      @Deprecated
      @Override
      public Long lastKey() {
         return this.lastLongKey();
      }
   }

   public static class Singleton extends Long2IntMaps.Singleton implements Long2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long key, int value, LongComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(long key, int value) {
         this(key, value, null);
      }

      final int compare(long k1, long k2) {
         return this.comparator == null ? Long.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public LongComparator comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractLong2IntMap.BasicEntry(this.key, this.value), Long2IntSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Long2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Integer>> entrySet() {
         return this.long2IntEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2IntSortedMap subMap(long from, long to) {
         return (Long2IntSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Long2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2IntSortedMap headMap(long to) {
         return (Long2IntSortedMap)(this.compare(this.key, to) < 0 ? this : Long2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2IntSortedMap tailMap(long from) {
         return (Long2IntSortedMap)(this.compare(from, this.key) <= 0 ? this : Long2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public long firstLongKey() {
         return this.key;
      }

      @Override
      public long lastLongKey() {
         return this.key;
      }

      @Deprecated
      @Override
      public Long2IntSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2IntSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2IntSortedMap subMap(Long ofrom, Long oto) {
         return this.subMap(ofrom.longValue(), oto.longValue());
      }

      @Deprecated
      @Override
      public Long firstKey() {
         return this.firstLongKey();
      }

      @Deprecated
      @Override
      public Long lastKey() {
         return this.lastLongKey();
      }
   }

   public static class SynchronizedSortedMap extends Long2IntMaps.SynchronizedMap implements Long2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2IntSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2IntSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Long2IntSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         synchronized (this.sync) {
            return this.sortedMap.comparator();
         }
      }

      @Override
      public ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Long2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Integer>> entrySet() {
         return this.long2IntEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2IntSortedMap subMap(long from, long to) {
         return new Long2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Long2IntSortedMap headMap(long to) {
         return new Long2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Long2IntSortedMap tailMap(long from) {
         return new Long2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }

      @Override
      public long firstLongKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstLongKey();
         }
      }

      @Override
      public long lastLongKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastLongKey();
         }
      }

      @Deprecated
      @Override
      public Long firstKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      @Deprecated
      @Override
      public Long lastKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      @Deprecated
      @Override
      public Long2IntSortedMap subMap(Long from, Long to) {
         return new Long2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Long2IntSortedMap headMap(Long to) {
         return new Long2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Long2IntSortedMap tailMap(Long from) {
         return new Long2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Long2IntMaps.UnmodifiableMap implements Long2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2IntSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2IntSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2IntEntrySet());
         }

         return (ObjectSortedSet<Long2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Integer>> entrySet() {
         return this.long2IntEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2IntSortedMap subMap(long from, long to) {
         return new Long2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Long2IntSortedMap headMap(long to) {
         return new Long2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Long2IntSortedMap tailMap(long from) {
         return new Long2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }

      @Override
      public long firstLongKey() {
         return this.sortedMap.firstLongKey();
      }

      @Override
      public long lastLongKey() {
         return this.sortedMap.lastLongKey();
      }

      @Deprecated
      @Override
      public Long firstKey() {
         return this.sortedMap.firstKey();
      }

      @Deprecated
      @Override
      public Long lastKey() {
         return this.sortedMap.lastKey();
      }

      @Deprecated
      @Override
      public Long2IntSortedMap subMap(Long from, Long to) {
         return new Long2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Long2IntSortedMap headMap(Long to) {
         return new Long2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Long2IntSortedMap tailMap(Long from) {
         return new Long2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
