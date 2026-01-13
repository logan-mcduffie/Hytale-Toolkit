package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Long2DoubleSortedMaps {
   public static final Long2DoubleSortedMaps.EmptySortedMap EMPTY_MAP = new Long2DoubleSortedMaps.EmptySortedMap();

   private Long2DoubleSortedMaps() {
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().longValue(), y.getKey().longValue());
   }

   public static ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator(Long2DoubleSortedMap map) {
      ObjectSortedSet<Long2DoubleMap.Entry> entries = map.long2DoubleEntrySet();
      return entries instanceof Long2DoubleSortedMap.FastSortedEntrySet
         ? ((Long2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Long2DoubleMap.Entry> fastIterable(Long2DoubleSortedMap map) {
      ObjectSortedSet<Long2DoubleMap.Entry> entries = map.long2DoubleEntrySet();
      return (ObjectBidirectionalIterable<Long2DoubleMap.Entry>)(entries instanceof Long2DoubleSortedMap.FastSortedEntrySet
         ? ((Long2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Long2DoubleSortedMap singleton(Long key, Double value) {
      return new Long2DoubleSortedMaps.Singleton(key, value);
   }

   public static Long2DoubleSortedMap singleton(Long key, Double value, LongComparator comparator) {
      return new Long2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2DoubleSortedMap singleton(long key, double value) {
      return new Long2DoubleSortedMaps.Singleton(key, value);
   }

   public static Long2DoubleSortedMap singleton(long key, double value, LongComparator comparator) {
      return new Long2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2DoubleSortedMap synchronize(Long2DoubleSortedMap m) {
      return new Long2DoubleSortedMaps.SynchronizedSortedMap(m);
   }

   public static Long2DoubleSortedMap synchronize(Long2DoubleSortedMap m, Object sync) {
      return new Long2DoubleSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Long2DoubleSortedMap unmodifiable(Long2DoubleSortedMap m) {
      return new Long2DoubleSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Long2DoubleMaps.EmptyMap implements Long2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Double>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      @Override
      public Long2DoubleSortedMap subMap(long from, long to) {
         return Long2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2DoubleSortedMap headMap(long to) {
         return Long2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2DoubleSortedMap tailMap(long from) {
         return Long2DoubleSortedMaps.EMPTY_MAP;
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
      public Long2DoubleSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2DoubleSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2DoubleSortedMap subMap(Long ofrom, Long oto) {
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

   public static class Singleton extends Long2DoubleMaps.Singleton implements Long2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long key, double value, LongComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(long key, double value) {
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
      public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractLong2DoubleMap.BasicEntry(this.key, this.value), Long2DoubleSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Long2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2DoubleSortedMap subMap(long from, long to) {
         return (Long2DoubleSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Long2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2DoubleSortedMap headMap(long to) {
         return (Long2DoubleSortedMap)(this.compare(this.key, to) < 0 ? this : Long2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2DoubleSortedMap tailMap(long from) {
         return (Long2DoubleSortedMap)(this.compare(from, this.key) <= 0 ? this : Long2DoubleSortedMaps.EMPTY_MAP);
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
      public Long2DoubleSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2DoubleSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2DoubleSortedMap subMap(Long ofrom, Long oto) {
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

   public static class SynchronizedSortedMap extends Long2DoubleMaps.SynchronizedMap implements Long2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2DoubleSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Long2DoubleSortedMap m) {
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
      public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2DoubleEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Long2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2DoubleSortedMap subMap(long from, long to) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Long2DoubleSortedMap headMap(long to) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Long2DoubleSortedMap tailMap(long from) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Long2DoubleSortedMap subMap(Long from, Long to) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Long2DoubleSortedMap headMap(Long to) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Long2DoubleSortedMap tailMap(Long from) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Long2DoubleMaps.UnmodifiableMap implements Long2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2DoubleEntrySet());
         }

         return (ObjectSortedSet<Long2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2DoubleSortedMap subMap(long from, long to) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Long2DoubleSortedMap headMap(long to) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Long2DoubleSortedMap tailMap(long from) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Long2DoubleSortedMap subMap(Long from, Long to) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Long2DoubleSortedMap headMap(Long to) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Long2DoubleSortedMap tailMap(Long from) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
