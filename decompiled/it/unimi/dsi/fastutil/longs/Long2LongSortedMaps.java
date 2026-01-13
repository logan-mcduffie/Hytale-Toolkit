package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Long2LongSortedMaps {
   public static final Long2LongSortedMaps.EmptySortedMap EMPTY_MAP = new Long2LongSortedMaps.EmptySortedMap();

   private Long2LongSortedMaps() {
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().longValue(), y.getKey().longValue());
   }

   public static ObjectBidirectionalIterator<Long2LongMap.Entry> fastIterator(Long2LongSortedMap map) {
      ObjectSortedSet<Long2LongMap.Entry> entries = map.long2LongEntrySet();
      return entries instanceof Long2LongSortedMap.FastSortedEntrySet ? ((Long2LongSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Long2LongMap.Entry> fastIterable(Long2LongSortedMap map) {
      ObjectSortedSet<Long2LongMap.Entry> entries = map.long2LongEntrySet();
      return (ObjectBidirectionalIterable<Long2LongMap.Entry>)(entries instanceof Long2LongSortedMap.FastSortedEntrySet
         ? ((Long2LongSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Long2LongSortedMap singleton(Long key, Long value) {
      return new Long2LongSortedMaps.Singleton(key, value);
   }

   public static Long2LongSortedMap singleton(Long key, Long value, LongComparator comparator) {
      return new Long2LongSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2LongSortedMap singleton(long key, long value) {
      return new Long2LongSortedMaps.Singleton(key, value);
   }

   public static Long2LongSortedMap singleton(long key, long value, LongComparator comparator) {
      return new Long2LongSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2LongSortedMap synchronize(Long2LongSortedMap m) {
      return new Long2LongSortedMaps.SynchronizedSortedMap(m);
   }

   public static Long2LongSortedMap synchronize(Long2LongSortedMap m, Object sync) {
      return new Long2LongSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Long2LongSortedMap unmodifiable(Long2LongSortedMap m) {
      return new Long2LongSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Long2LongMaps.EmptyMap implements Long2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Long>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      @Override
      public Long2LongSortedMap subMap(long from, long to) {
         return Long2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2LongSortedMap headMap(long to) {
         return Long2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2LongSortedMap tailMap(long from) {
         return Long2LongSortedMaps.EMPTY_MAP;
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
      public Long2LongSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2LongSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2LongSortedMap subMap(Long ofrom, Long oto) {
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

   public static class Singleton extends Long2LongMaps.Singleton implements Long2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long key, long value, LongComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(long key, long value) {
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
      public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractLong2LongMap.BasicEntry(this.key, this.value), Long2LongSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Long2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2LongSortedMap subMap(long from, long to) {
         return (Long2LongSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Long2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2LongSortedMap headMap(long to) {
         return (Long2LongSortedMap)(this.compare(this.key, to) < 0 ? this : Long2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2LongSortedMap tailMap(long from) {
         return (Long2LongSortedMap)(this.compare(from, this.key) <= 0 ? this : Long2LongSortedMaps.EMPTY_MAP);
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
      public Long2LongSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2LongSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2LongSortedMap subMap(Long ofrom, Long oto) {
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

   public static class SynchronizedSortedMap extends Long2LongMaps.SynchronizedMap implements Long2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2LongSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2LongSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Long2LongSortedMap m) {
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
      public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2LongEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Long2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2LongSortedMap subMap(long from, long to) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Long2LongSortedMap headMap(long to) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Long2LongSortedMap tailMap(long from) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Long2LongSortedMap subMap(Long from, Long to) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Long2LongSortedMap headMap(Long to) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Long2LongSortedMap tailMap(Long from) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Long2LongMaps.UnmodifiableMap implements Long2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2LongSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2LongSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2LongEntrySet());
         }

         return (ObjectSortedSet<Long2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2LongSortedMap subMap(long from, long to) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Long2LongSortedMap headMap(long to) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Long2LongSortedMap tailMap(long from) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Long2LongSortedMap subMap(Long from, Long to) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Long2LongSortedMap headMap(Long to) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Long2LongSortedMap tailMap(Long from) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
