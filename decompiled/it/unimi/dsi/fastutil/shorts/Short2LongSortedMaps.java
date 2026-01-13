package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2LongSortedMaps {
   public static final Short2LongSortedMaps.EmptySortedMap EMPTY_MAP = new Short2LongSortedMaps.EmptySortedMap();

   private Short2LongSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static ObjectBidirectionalIterator<Short2LongMap.Entry> fastIterator(Short2LongSortedMap map) {
      ObjectSortedSet<Short2LongMap.Entry> entries = map.short2LongEntrySet();
      return entries instanceof Short2LongSortedMap.FastSortedEntrySet ? ((Short2LongSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Short2LongMap.Entry> fastIterable(Short2LongSortedMap map) {
      ObjectSortedSet<Short2LongMap.Entry> entries = map.short2LongEntrySet();
      return (ObjectBidirectionalIterable<Short2LongMap.Entry>)(entries instanceof Short2LongSortedMap.FastSortedEntrySet
         ? ((Short2LongSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Short2LongSortedMap singleton(Short key, Long value) {
      return new Short2LongSortedMaps.Singleton(key, value);
   }

   public static Short2LongSortedMap singleton(Short key, Long value, ShortComparator comparator) {
      return new Short2LongSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2LongSortedMap singleton(short key, long value) {
      return new Short2LongSortedMaps.Singleton(key, value);
   }

   public static Short2LongSortedMap singleton(short key, long value, ShortComparator comparator) {
      return new Short2LongSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2LongSortedMap synchronize(Short2LongSortedMap m) {
      return new Short2LongSortedMaps.SynchronizedSortedMap(m);
   }

   public static Short2LongSortedMap synchronize(Short2LongSortedMap m, Object sync) {
      return new Short2LongSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Short2LongSortedMap unmodifiable(Short2LongSortedMap m) {
      return new Short2LongSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Short2LongMaps.EmptyMap implements Short2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Long>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public Short2LongSortedMap subMap(short from, short to) {
         return Short2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2LongSortedMap headMap(short to) {
         return Short2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2LongSortedMap tailMap(short from) {
         return Short2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public short firstShortKey() {
         throw new NoSuchElementException();
      }

      @Override
      public short lastShortKey() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Short2LongSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2LongSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2LongSortedMap subMap(Short ofrom, Short oto) {
         return this.subMap(ofrom.shortValue(), oto.shortValue());
      }

      @Deprecated
      @Override
      public Short firstKey() {
         return this.firstShortKey();
      }

      @Deprecated
      @Override
      public Short lastKey() {
         return this.lastShortKey();
      }
   }

   public static class Singleton extends Short2LongMaps.Singleton implements Short2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short key, long value, ShortComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(short key, long value) {
         this(key, value, null);
      }

      final int compare(short k1, short k2) {
         return this.comparator == null ? Short.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public ShortComparator comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2LongMap.BasicEntry(this.key, this.value), Short2LongSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Long>> entrySet() {
         return this.short2LongEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2LongSortedMap subMap(short from, short to) {
         return (Short2LongSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2LongSortedMap headMap(short to) {
         return (Short2LongSortedMap)(this.compare(this.key, to) < 0 ? this : Short2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2LongSortedMap tailMap(short from) {
         return (Short2LongSortedMap)(this.compare(from, this.key) <= 0 ? this : Short2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public short firstShortKey() {
         return this.key;
      }

      @Override
      public short lastShortKey() {
         return this.key;
      }

      @Deprecated
      @Override
      public Short2LongSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2LongSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2LongSortedMap subMap(Short ofrom, Short oto) {
         return this.subMap(ofrom.shortValue(), oto.shortValue());
      }

      @Deprecated
      @Override
      public Short firstKey() {
         return this.firstShortKey();
      }

      @Deprecated
      @Override
      public Short lastKey() {
         return this.lastShortKey();
      }
   }

   public static class SynchronizedSortedMap extends Short2LongMaps.SynchronizedMap implements Short2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2LongSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2LongSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2LongSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         synchronized (this.sync) {
            return this.sortedMap.comparator();
         }
      }

      @Override
      public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2LongEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Long>> entrySet() {
         return this.short2LongEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2LongSortedMap subMap(short from, short to) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2LongSortedMap headMap(short to) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2LongSortedMap tailMap(short from) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }

      @Override
      public short firstShortKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstShortKey();
         }
      }

      @Override
      public short lastShortKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastShortKey();
         }
      }

      @Deprecated
      @Override
      public Short firstKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      @Deprecated
      @Override
      public Short lastKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      @Deprecated
      @Override
      public Short2LongSortedMap subMap(Short from, Short to) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2LongSortedMap headMap(Short to) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2LongSortedMap tailMap(Short from) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Short2LongMaps.UnmodifiableMap implements Short2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2LongSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2LongSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2LongEntrySet());
         }

         return (ObjectSortedSet<Short2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Long>> entrySet() {
         return this.short2LongEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2LongSortedMap subMap(short from, short to) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2LongSortedMap headMap(short to) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Short2LongSortedMap tailMap(short from) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }

      @Override
      public short firstShortKey() {
         return this.sortedMap.firstShortKey();
      }

      @Override
      public short lastShortKey() {
         return this.sortedMap.lastShortKey();
      }

      @Deprecated
      @Override
      public Short firstKey() {
         return this.sortedMap.firstKey();
      }

      @Deprecated
      @Override
      public Short lastKey() {
         return this.sortedMap.lastKey();
      }

      @Deprecated
      @Override
      public Short2LongSortedMap subMap(Short from, Short to) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2LongSortedMap headMap(Short to) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2LongSortedMap tailMap(Short from) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
