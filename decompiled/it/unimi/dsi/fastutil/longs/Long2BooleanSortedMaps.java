package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Long2BooleanSortedMaps {
   public static final Long2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Long2BooleanSortedMaps.EmptySortedMap();

   private Long2BooleanSortedMaps() {
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().longValue(), y.getKey().longValue());
   }

   public static ObjectBidirectionalIterator<Long2BooleanMap.Entry> fastIterator(Long2BooleanSortedMap map) {
      ObjectSortedSet<Long2BooleanMap.Entry> entries = map.long2BooleanEntrySet();
      return entries instanceof Long2BooleanSortedMap.FastSortedEntrySet
         ? ((Long2BooleanSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Long2BooleanMap.Entry> fastIterable(Long2BooleanSortedMap map) {
      ObjectSortedSet<Long2BooleanMap.Entry> entries = map.long2BooleanEntrySet();
      return (ObjectBidirectionalIterable<Long2BooleanMap.Entry>)(entries instanceof Long2BooleanSortedMap.FastSortedEntrySet
         ? ((Long2BooleanSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Long2BooleanSortedMap singleton(Long key, Boolean value) {
      return new Long2BooleanSortedMaps.Singleton(key, value);
   }

   public static Long2BooleanSortedMap singleton(Long key, Boolean value, LongComparator comparator) {
      return new Long2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2BooleanSortedMap singleton(long key, boolean value) {
      return new Long2BooleanSortedMaps.Singleton(key, value);
   }

   public static Long2BooleanSortedMap singleton(long key, boolean value, LongComparator comparator) {
      return new Long2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2BooleanSortedMap synchronize(Long2BooleanSortedMap m) {
      return new Long2BooleanSortedMaps.SynchronizedSortedMap(m);
   }

   public static Long2BooleanSortedMap synchronize(Long2BooleanSortedMap m, Object sync) {
      return new Long2BooleanSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Long2BooleanSortedMap unmodifiable(Long2BooleanSortedMap m) {
      return new Long2BooleanSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Long2BooleanMaps.EmptyMap implements Long2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      @Override
      public Long2BooleanSortedMap subMap(long from, long to) {
         return Long2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2BooleanSortedMap headMap(long to) {
         return Long2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2BooleanSortedMap tailMap(long from) {
         return Long2BooleanSortedMaps.EMPTY_MAP;
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
      public Long2BooleanSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2BooleanSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2BooleanSortedMap subMap(Long ofrom, Long oto) {
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

   public static class Singleton extends Long2BooleanMaps.Singleton implements Long2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long key, boolean value, LongComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(long key, boolean value) {
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
      public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractLong2BooleanMap.BasicEntry(this.key, this.value), Long2BooleanSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Long2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2BooleanSortedMap subMap(long from, long to) {
         return (Long2BooleanSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Long2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2BooleanSortedMap headMap(long to) {
         return (Long2BooleanSortedMap)(this.compare(this.key, to) < 0 ? this : Long2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2BooleanSortedMap tailMap(long from) {
         return (Long2BooleanSortedMap)(this.compare(from, this.key) <= 0 ? this : Long2BooleanSortedMaps.EMPTY_MAP);
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
      public Long2BooleanSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2BooleanSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2BooleanSortedMap subMap(Long ofrom, Long oto) {
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

   public static class SynchronizedSortedMap extends Long2BooleanMaps.SynchronizedMap implements Long2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2BooleanSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Long2BooleanSortedMap m) {
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
      public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Long2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2BooleanSortedMap subMap(long from, long to) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Long2BooleanSortedMap headMap(long to) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Long2BooleanSortedMap tailMap(long from) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Long2BooleanSortedMap subMap(Long from, Long to) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Long2BooleanSortedMap headMap(Long to) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Long2BooleanSortedMap tailMap(Long from) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Long2BooleanMaps.UnmodifiableMap implements Long2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2BooleanSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2BooleanEntrySet());
         }

         return (ObjectSortedSet<Long2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2BooleanSortedMap subMap(long from, long to) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Long2BooleanSortedMap headMap(long to) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Long2BooleanSortedMap tailMap(long from) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Long2BooleanSortedMap subMap(Long from, Long to) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Long2BooleanSortedMap headMap(Long to) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Long2BooleanSortedMap tailMap(Long from) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
