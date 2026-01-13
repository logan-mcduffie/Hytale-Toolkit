package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Long2ShortSortedMaps {
   public static final Long2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Long2ShortSortedMaps.EmptySortedMap();

   private Long2ShortSortedMaps() {
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().longValue(), y.getKey().longValue());
   }

   public static ObjectBidirectionalIterator<Long2ShortMap.Entry> fastIterator(Long2ShortSortedMap map) {
      ObjectSortedSet<Long2ShortMap.Entry> entries = map.long2ShortEntrySet();
      return entries instanceof Long2ShortSortedMap.FastSortedEntrySet ? ((Long2ShortSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Long2ShortMap.Entry> fastIterable(Long2ShortSortedMap map) {
      ObjectSortedSet<Long2ShortMap.Entry> entries = map.long2ShortEntrySet();
      return (ObjectBidirectionalIterable<Long2ShortMap.Entry>)(entries instanceof Long2ShortSortedMap.FastSortedEntrySet
         ? ((Long2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Long2ShortSortedMap singleton(Long key, Short value) {
      return new Long2ShortSortedMaps.Singleton(key, value);
   }

   public static Long2ShortSortedMap singleton(Long key, Short value, LongComparator comparator) {
      return new Long2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2ShortSortedMap singleton(long key, short value) {
      return new Long2ShortSortedMaps.Singleton(key, value);
   }

   public static Long2ShortSortedMap singleton(long key, short value, LongComparator comparator) {
      return new Long2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2ShortSortedMap synchronize(Long2ShortSortedMap m) {
      return new Long2ShortSortedMaps.SynchronizedSortedMap(m);
   }

   public static Long2ShortSortedMap synchronize(Long2ShortSortedMap m, Object sync) {
      return new Long2ShortSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Long2ShortSortedMap unmodifiable(Long2ShortSortedMap m) {
      return new Long2ShortSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Long2ShortMaps.EmptyMap implements Long2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      @Override
      public Long2ShortSortedMap subMap(long from, long to) {
         return Long2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2ShortSortedMap headMap(long to) {
         return Long2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2ShortSortedMap tailMap(long from) {
         return Long2ShortSortedMaps.EMPTY_MAP;
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
      public Long2ShortSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2ShortSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2ShortSortedMap subMap(Long ofrom, Long oto) {
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

   public static class Singleton extends Long2ShortMaps.Singleton implements Long2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long key, short value, LongComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(long key, short value) {
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
      public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractLong2ShortMap.BasicEntry(this.key, this.value), Long2ShortSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Long2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2ShortSortedMap subMap(long from, long to) {
         return (Long2ShortSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Long2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2ShortSortedMap headMap(long to) {
         return (Long2ShortSortedMap)(this.compare(this.key, to) < 0 ? this : Long2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2ShortSortedMap tailMap(long from) {
         return (Long2ShortSortedMap)(this.compare(from, this.key) <= 0 ? this : Long2ShortSortedMaps.EMPTY_MAP);
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
      public Long2ShortSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2ShortSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2ShortSortedMap subMap(Long ofrom, Long oto) {
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

   public static class SynchronizedSortedMap extends Long2ShortMaps.SynchronizedMap implements Long2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2ShortSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Long2ShortSortedMap m) {
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
      public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Long2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2ShortSortedMap subMap(long from, long to) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Long2ShortSortedMap headMap(long to) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Long2ShortSortedMap tailMap(long from) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Long2ShortSortedMap subMap(Long from, Long to) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Long2ShortSortedMap headMap(Long to) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Long2ShortSortedMap tailMap(Long from) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Long2ShortMaps.UnmodifiableMap implements Long2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2ShortSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2ShortEntrySet());
         }

         return (ObjectSortedSet<Long2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2ShortSortedMap subMap(long from, long to) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Long2ShortSortedMap headMap(long to) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Long2ShortSortedMap tailMap(long from) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Long2ShortSortedMap subMap(Long from, Long to) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Long2ShortSortedMap headMap(Long to) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Long2ShortSortedMap tailMap(Long from) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
