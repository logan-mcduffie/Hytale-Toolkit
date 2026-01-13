package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Long2FloatSortedMaps {
   public static final Long2FloatSortedMaps.EmptySortedMap EMPTY_MAP = new Long2FloatSortedMaps.EmptySortedMap();

   private Long2FloatSortedMaps() {
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().longValue(), y.getKey().longValue());
   }

   public static ObjectBidirectionalIterator<Long2FloatMap.Entry> fastIterator(Long2FloatSortedMap map) {
      ObjectSortedSet<Long2FloatMap.Entry> entries = map.long2FloatEntrySet();
      return entries instanceof Long2FloatSortedMap.FastSortedEntrySet ? ((Long2FloatSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Long2FloatMap.Entry> fastIterable(Long2FloatSortedMap map) {
      ObjectSortedSet<Long2FloatMap.Entry> entries = map.long2FloatEntrySet();
      return (ObjectBidirectionalIterable<Long2FloatMap.Entry>)(entries instanceof Long2FloatSortedMap.FastSortedEntrySet
         ? ((Long2FloatSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Long2FloatSortedMap singleton(Long key, Float value) {
      return new Long2FloatSortedMaps.Singleton(key, value);
   }

   public static Long2FloatSortedMap singleton(Long key, Float value, LongComparator comparator) {
      return new Long2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2FloatSortedMap singleton(long key, float value) {
      return new Long2FloatSortedMaps.Singleton(key, value);
   }

   public static Long2FloatSortedMap singleton(long key, float value, LongComparator comparator) {
      return new Long2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2FloatSortedMap synchronize(Long2FloatSortedMap m) {
      return new Long2FloatSortedMaps.SynchronizedSortedMap(m);
   }

   public static Long2FloatSortedMap synchronize(Long2FloatSortedMap m, Object sync) {
      return new Long2FloatSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Long2FloatSortedMap unmodifiable(Long2FloatSortedMap m) {
      return new Long2FloatSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Long2FloatMaps.EmptyMap implements Long2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Float>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      @Override
      public Long2FloatSortedMap subMap(long from, long to) {
         return Long2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2FloatSortedMap headMap(long to) {
         return Long2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2FloatSortedMap tailMap(long from) {
         return Long2FloatSortedMaps.EMPTY_MAP;
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
      public Long2FloatSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2FloatSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2FloatSortedMap subMap(Long ofrom, Long oto) {
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

   public static class Singleton extends Long2FloatMaps.Singleton implements Long2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long key, float value, LongComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(long key, float value) {
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
      public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractLong2FloatMap.BasicEntry(this.key, this.value), Long2FloatSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Long2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Float>> entrySet() {
         return this.long2FloatEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2FloatSortedMap subMap(long from, long to) {
         return (Long2FloatSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Long2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2FloatSortedMap headMap(long to) {
         return (Long2FloatSortedMap)(this.compare(this.key, to) < 0 ? this : Long2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2FloatSortedMap tailMap(long from) {
         return (Long2FloatSortedMap)(this.compare(from, this.key) <= 0 ? this : Long2FloatSortedMaps.EMPTY_MAP);
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
      public Long2FloatSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2FloatSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2FloatSortedMap subMap(Long ofrom, Long oto) {
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

   public static class SynchronizedSortedMap extends Long2FloatMaps.SynchronizedMap implements Long2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2FloatSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2FloatSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Long2FloatSortedMap m) {
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
      public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2FloatEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Long2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Float>> entrySet() {
         return this.long2FloatEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2FloatSortedMap subMap(long from, long to) {
         return new Long2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Long2FloatSortedMap headMap(long to) {
         return new Long2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Long2FloatSortedMap tailMap(long from) {
         return new Long2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Long2FloatSortedMap subMap(Long from, Long to) {
         return new Long2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Long2FloatSortedMap headMap(Long to) {
         return new Long2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Long2FloatSortedMap tailMap(Long from) {
         return new Long2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Long2FloatMaps.UnmodifiableMap implements Long2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2FloatSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2FloatSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2FloatEntrySet());
         }

         return (ObjectSortedSet<Long2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Float>> entrySet() {
         return this.long2FloatEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2FloatSortedMap subMap(long from, long to) {
         return new Long2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Long2FloatSortedMap headMap(long to) {
         return new Long2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Long2FloatSortedMap tailMap(long from) {
         return new Long2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Long2FloatSortedMap subMap(Long from, Long to) {
         return new Long2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Long2FloatSortedMap headMap(Long to) {
         return new Long2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Long2FloatSortedMap tailMap(Long from) {
         return new Long2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
