package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Long2CharSortedMaps {
   public static final Long2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Long2CharSortedMaps.EmptySortedMap();

   private Long2CharSortedMaps() {
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().longValue(), y.getKey().longValue());
   }

   public static ObjectBidirectionalIterator<Long2CharMap.Entry> fastIterator(Long2CharSortedMap map) {
      ObjectSortedSet<Long2CharMap.Entry> entries = map.long2CharEntrySet();
      return entries instanceof Long2CharSortedMap.FastSortedEntrySet ? ((Long2CharSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Long2CharMap.Entry> fastIterable(Long2CharSortedMap map) {
      ObjectSortedSet<Long2CharMap.Entry> entries = map.long2CharEntrySet();
      return (ObjectBidirectionalIterable<Long2CharMap.Entry>)(entries instanceof Long2CharSortedMap.FastSortedEntrySet
         ? ((Long2CharSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Long2CharSortedMap singleton(Long key, Character value) {
      return new Long2CharSortedMaps.Singleton(key, value);
   }

   public static Long2CharSortedMap singleton(Long key, Character value, LongComparator comparator) {
      return new Long2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2CharSortedMap singleton(long key, char value) {
      return new Long2CharSortedMaps.Singleton(key, value);
   }

   public static Long2CharSortedMap singleton(long key, char value, LongComparator comparator) {
      return new Long2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2CharSortedMap synchronize(Long2CharSortedMap m) {
      return new Long2CharSortedMaps.SynchronizedSortedMap(m);
   }

   public static Long2CharSortedMap synchronize(Long2CharSortedMap m, Object sync) {
      return new Long2CharSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Long2CharSortedMap unmodifiable(Long2CharSortedMap m) {
      return new Long2CharSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Long2CharMaps.EmptyMap implements Long2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Long2CharMap.Entry> long2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      @Override
      public Long2CharSortedMap subMap(long from, long to) {
         return Long2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2CharSortedMap headMap(long to) {
         return Long2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2CharSortedMap tailMap(long from) {
         return Long2CharSortedMaps.EMPTY_MAP;
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
      public Long2CharSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2CharSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2CharSortedMap subMap(Long ofrom, Long oto) {
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

   public static class Singleton extends Long2CharMaps.Singleton implements Long2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long key, char value, LongComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(long key, char value) {
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
      public ObjectSortedSet<Long2CharMap.Entry> long2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractLong2CharMap.BasicEntry(this.key, this.value), Long2CharSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Long2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Character>> entrySet() {
         return this.long2CharEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2CharSortedMap subMap(long from, long to) {
         return (Long2CharSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Long2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2CharSortedMap headMap(long to) {
         return (Long2CharSortedMap)(this.compare(this.key, to) < 0 ? this : Long2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2CharSortedMap tailMap(long from) {
         return (Long2CharSortedMap)(this.compare(from, this.key) <= 0 ? this : Long2CharSortedMaps.EMPTY_MAP);
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
      public Long2CharSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2CharSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2CharSortedMap subMap(Long ofrom, Long oto) {
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

   public static class SynchronizedSortedMap extends Long2CharMaps.SynchronizedMap implements Long2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2CharSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Long2CharSortedMap m) {
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
      public ObjectSortedSet<Long2CharMap.Entry> long2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Long2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Character>> entrySet() {
         return this.long2CharEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2CharSortedMap subMap(long from, long to) {
         return new Long2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Long2CharSortedMap headMap(long to) {
         return new Long2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Long2CharSortedMap tailMap(long from) {
         return new Long2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Long2CharSortedMap subMap(Long from, Long to) {
         return new Long2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Long2CharSortedMap headMap(Long to) {
         return new Long2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Long2CharSortedMap tailMap(Long from) {
         return new Long2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Long2CharMaps.UnmodifiableMap implements Long2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2CharSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Long2CharMap.Entry> long2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2CharEntrySet());
         }

         return (ObjectSortedSet<Long2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Character>> entrySet() {
         return this.long2CharEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2CharSortedMap subMap(long from, long to) {
         return new Long2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Long2CharSortedMap headMap(long to) {
         return new Long2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Long2CharSortedMap tailMap(long from) {
         return new Long2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Long2CharSortedMap subMap(Long from, Long to) {
         return new Long2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Long2CharSortedMap headMap(Long to) {
         return new Long2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Long2CharSortedMap tailMap(Long from) {
         return new Long2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
