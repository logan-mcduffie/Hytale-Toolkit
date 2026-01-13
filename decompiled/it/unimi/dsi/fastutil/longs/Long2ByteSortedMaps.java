package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Long2ByteSortedMaps {
   public static final Long2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Long2ByteSortedMaps.EmptySortedMap();

   private Long2ByteSortedMaps() {
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().longValue(), y.getKey().longValue());
   }

   public static ObjectBidirectionalIterator<Long2ByteMap.Entry> fastIterator(Long2ByteSortedMap map) {
      ObjectSortedSet<Long2ByteMap.Entry> entries = map.long2ByteEntrySet();
      return entries instanceof Long2ByteSortedMap.FastSortedEntrySet ? ((Long2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Long2ByteMap.Entry> fastIterable(Long2ByteSortedMap map) {
      ObjectSortedSet<Long2ByteMap.Entry> entries = map.long2ByteEntrySet();
      return (ObjectBidirectionalIterable<Long2ByteMap.Entry>)(entries instanceof Long2ByteSortedMap.FastSortedEntrySet
         ? ((Long2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Long2ByteSortedMap singleton(Long key, Byte value) {
      return new Long2ByteSortedMaps.Singleton(key, value);
   }

   public static Long2ByteSortedMap singleton(Long key, Byte value, LongComparator comparator) {
      return new Long2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2ByteSortedMap singleton(long key, byte value) {
      return new Long2ByteSortedMaps.Singleton(key, value);
   }

   public static Long2ByteSortedMap singleton(long key, byte value, LongComparator comparator) {
      return new Long2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Long2ByteSortedMap synchronize(Long2ByteSortedMap m) {
      return new Long2ByteSortedMaps.SynchronizedSortedMap(m);
   }

   public static Long2ByteSortedMap synchronize(Long2ByteSortedMap m, Object sync) {
      return new Long2ByteSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Long2ByteSortedMap unmodifiable(Long2ByteSortedMap m) {
      return new Long2ByteSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Long2ByteMaps.EmptyMap implements Long2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Long2ByteMap.Entry> long2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      @Override
      public Long2ByteSortedMap subMap(long from, long to) {
         return Long2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2ByteSortedMap headMap(long to) {
         return Long2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Long2ByteSortedMap tailMap(long from) {
         return Long2ByteSortedMaps.EMPTY_MAP;
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
      public Long2ByteSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2ByteSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2ByteSortedMap subMap(Long ofrom, Long oto) {
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

   public static class Singleton extends Long2ByteMaps.Singleton implements Long2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long key, byte value, LongComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(long key, byte value) {
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
      public ObjectSortedSet<Long2ByteMap.Entry> long2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractLong2ByteMap.BasicEntry(this.key, this.value), Long2ByteSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Long2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Byte>> entrySet() {
         return this.long2ByteEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2ByteSortedMap subMap(long from, long to) {
         return (Long2ByteSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Long2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2ByteSortedMap headMap(long to) {
         return (Long2ByteSortedMap)(this.compare(this.key, to) < 0 ? this : Long2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Long2ByteSortedMap tailMap(long from) {
         return (Long2ByteSortedMap)(this.compare(from, this.key) <= 0 ? this : Long2ByteSortedMaps.EMPTY_MAP);
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
      public Long2ByteSortedMap headMap(Long oto) {
         return this.headMap(oto.longValue());
      }

      @Deprecated
      @Override
      public Long2ByteSortedMap tailMap(Long ofrom) {
         return this.tailMap(ofrom.longValue());
      }

      @Deprecated
      @Override
      public Long2ByteSortedMap subMap(Long ofrom, Long oto) {
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

   public static class SynchronizedSortedMap extends Long2ByteMaps.SynchronizedMap implements Long2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ByteSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2ByteSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Long2ByteSortedMap m) {
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
      public ObjectSortedSet<Long2ByteMap.Entry> long2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Long2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Byte>> entrySet() {
         return this.long2ByteEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2ByteSortedMap subMap(long from, long to) {
         return new Long2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Long2ByteSortedMap headMap(long to) {
         return new Long2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Long2ByteSortedMap tailMap(long from) {
         return new Long2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Long2ByteSortedMap subMap(Long from, Long to) {
         return new Long2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Long2ByteSortedMap headMap(Long to) {
         return new Long2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Long2ByteSortedMap tailMap(Long from) {
         return new Long2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Long2ByteMaps.UnmodifiableMap implements Long2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ByteSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2ByteSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Long2ByteMap.Entry> long2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2ByteEntrySet());
         }

         return (ObjectSortedSet<Long2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Long, Byte>> entrySet() {
         return this.long2ByteEntrySet();
      }

      @Override
      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      @Override
      public Long2ByteSortedMap subMap(long from, long to) {
         return new Long2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Long2ByteSortedMap headMap(long to) {
         return new Long2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Long2ByteSortedMap tailMap(long from) {
         return new Long2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Long2ByteSortedMap subMap(Long from, Long to) {
         return new Long2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Long2ByteSortedMap headMap(Long to) {
         return new Long2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Long2ByteSortedMap tailMap(Long from) {
         return new Long2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
