package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2IntSortedMaps {
   public static final Short2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Short2IntSortedMaps.EmptySortedMap();

   private Short2IntSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static ObjectBidirectionalIterator<Short2IntMap.Entry> fastIterator(Short2IntSortedMap map) {
      ObjectSortedSet<Short2IntMap.Entry> entries = map.short2IntEntrySet();
      return entries instanceof Short2IntSortedMap.FastSortedEntrySet ? ((Short2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Short2IntMap.Entry> fastIterable(Short2IntSortedMap map) {
      ObjectSortedSet<Short2IntMap.Entry> entries = map.short2IntEntrySet();
      return (ObjectBidirectionalIterable<Short2IntMap.Entry>)(entries instanceof Short2IntSortedMap.FastSortedEntrySet
         ? ((Short2IntSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Short2IntSortedMap singleton(Short key, Integer value) {
      return new Short2IntSortedMaps.Singleton(key, value);
   }

   public static Short2IntSortedMap singleton(Short key, Integer value, ShortComparator comparator) {
      return new Short2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2IntSortedMap singleton(short key, int value) {
      return new Short2IntSortedMaps.Singleton(key, value);
   }

   public static Short2IntSortedMap singleton(short key, int value, ShortComparator comparator) {
      return new Short2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2IntSortedMap synchronize(Short2IntSortedMap m) {
      return new Short2IntSortedMaps.SynchronizedSortedMap(m);
   }

   public static Short2IntSortedMap synchronize(Short2IntSortedMap m, Object sync) {
      return new Short2IntSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Short2IntSortedMap unmodifiable(Short2IntSortedMap m) {
      return new Short2IntSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Short2IntMaps.EmptyMap implements Short2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2IntMap.Entry> short2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public Short2IntSortedMap subMap(short from, short to) {
         return Short2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2IntSortedMap headMap(short to) {
         return Short2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2IntSortedMap tailMap(short from) {
         return Short2IntSortedMaps.EMPTY_MAP;
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
      public Short2IntSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2IntSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2IntSortedMap subMap(Short ofrom, Short oto) {
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

   public static class Singleton extends Short2IntMaps.Singleton implements Short2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short key, int value, ShortComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(short key, int value) {
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
      public ObjectSortedSet<Short2IntMap.Entry> short2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2IntMap.BasicEntry(this.key, this.value), Short2IntSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2IntSortedMap subMap(short from, short to) {
         return (Short2IntSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2IntSortedMap headMap(short to) {
         return (Short2IntSortedMap)(this.compare(this.key, to) < 0 ? this : Short2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2IntSortedMap tailMap(short from) {
         return (Short2IntSortedMap)(this.compare(from, this.key) <= 0 ? this : Short2IntSortedMaps.EMPTY_MAP);
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
      public Short2IntSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2IntSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2IntSortedMap subMap(Short ofrom, Short oto) {
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

   public static class SynchronizedSortedMap extends Short2IntMaps.SynchronizedMap implements Short2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2IntSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2IntSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2IntSortedMap m) {
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
      public ObjectSortedSet<Short2IntMap.Entry> short2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2IntSortedMap subMap(short from, short to) {
         return new Short2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2IntSortedMap headMap(short to) {
         return new Short2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2IntSortedMap tailMap(short from) {
         return new Short2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Short2IntSortedMap subMap(Short from, Short to) {
         return new Short2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2IntSortedMap headMap(Short to) {
         return new Short2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2IntSortedMap tailMap(Short from) {
         return new Short2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Short2IntMaps.UnmodifiableMap implements Short2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2IntSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2IntSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2IntMap.Entry> short2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2IntEntrySet());
         }

         return (ObjectSortedSet<Short2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2IntSortedMap subMap(short from, short to) {
         return new Short2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2IntSortedMap headMap(short to) {
         return new Short2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Short2IntSortedMap tailMap(short from) {
         return new Short2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Short2IntSortedMap subMap(Short from, Short to) {
         return new Short2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2IntSortedMap headMap(Short to) {
         return new Short2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2IntSortedMap tailMap(Short from) {
         return new Short2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
