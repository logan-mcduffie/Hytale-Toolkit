package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2ShortSortedMaps {
   public static final Short2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Short2ShortSortedMaps.EmptySortedMap();

   private Short2ShortSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static ObjectBidirectionalIterator<Short2ShortMap.Entry> fastIterator(Short2ShortSortedMap map) {
      ObjectSortedSet<Short2ShortMap.Entry> entries = map.short2ShortEntrySet();
      return entries instanceof Short2ShortSortedMap.FastSortedEntrySet
         ? ((Short2ShortSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Short2ShortMap.Entry> fastIterable(Short2ShortSortedMap map) {
      ObjectSortedSet<Short2ShortMap.Entry> entries = map.short2ShortEntrySet();
      return (ObjectBidirectionalIterable<Short2ShortMap.Entry>)(entries instanceof Short2ShortSortedMap.FastSortedEntrySet
         ? ((Short2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Short2ShortSortedMap singleton(Short key, Short value) {
      return new Short2ShortSortedMaps.Singleton(key, value);
   }

   public static Short2ShortSortedMap singleton(Short key, Short value, ShortComparator comparator) {
      return new Short2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2ShortSortedMap singleton(short key, short value) {
      return new Short2ShortSortedMaps.Singleton(key, value);
   }

   public static Short2ShortSortedMap singleton(short key, short value, ShortComparator comparator) {
      return new Short2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2ShortSortedMap synchronize(Short2ShortSortedMap m) {
      return new Short2ShortSortedMaps.SynchronizedSortedMap(m);
   }

   public static Short2ShortSortedMap synchronize(Short2ShortSortedMap m, Object sync) {
      return new Short2ShortSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Short2ShortSortedMap unmodifiable(Short2ShortSortedMap m) {
      return new Short2ShortSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Short2ShortMaps.EmptyMap implements Short2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2ShortMap.Entry> short2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public Short2ShortSortedMap subMap(short from, short to) {
         return Short2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2ShortSortedMap headMap(short to) {
         return Short2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2ShortSortedMap tailMap(short from) {
         return Short2ShortSortedMaps.EMPTY_MAP;
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
      public Short2ShortSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2ShortSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2ShortSortedMap subMap(Short ofrom, Short oto) {
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

   public static class Singleton extends Short2ShortMaps.Singleton implements Short2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short key, short value, ShortComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(short key, short value) {
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
      public ObjectSortedSet<Short2ShortMap.Entry> short2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2ShortMap.BasicEntry(this.key, this.value), Short2ShortSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Short>> entrySet() {
         return this.short2ShortEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ShortSortedMap subMap(short from, short to) {
         return (Short2ShortSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2ShortSortedMap headMap(short to) {
         return (Short2ShortSortedMap)(this.compare(this.key, to) < 0 ? this : Short2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2ShortSortedMap tailMap(short from) {
         return (Short2ShortSortedMap)(this.compare(from, this.key) <= 0 ? this : Short2ShortSortedMaps.EMPTY_MAP);
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
      public Short2ShortSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2ShortSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2ShortSortedMap subMap(Short ofrom, Short oto) {
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

   public static class SynchronizedSortedMap extends Short2ShortMaps.SynchronizedMap implements Short2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ShortSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2ShortSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2ShortSortedMap m) {
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
      public ObjectSortedSet<Short2ShortMap.Entry> short2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Short>> entrySet() {
         return this.short2ShortEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ShortSortedMap subMap(short from, short to) {
         return new Short2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2ShortSortedMap headMap(short to) {
         return new Short2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2ShortSortedMap tailMap(short from) {
         return new Short2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Short2ShortSortedMap subMap(Short from, Short to) {
         return new Short2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2ShortSortedMap headMap(Short to) {
         return new Short2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2ShortSortedMap tailMap(Short from) {
         return new Short2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Short2ShortMaps.UnmodifiableMap implements Short2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ShortSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2ShortSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2ShortMap.Entry> short2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2ShortEntrySet());
         }

         return (ObjectSortedSet<Short2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Short>> entrySet() {
         return this.short2ShortEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ShortSortedMap subMap(short from, short to) {
         return new Short2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2ShortSortedMap headMap(short to) {
         return new Short2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Short2ShortSortedMap tailMap(short from) {
         return new Short2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Short2ShortSortedMap subMap(Short from, Short to) {
         return new Short2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2ShortSortedMap headMap(Short to) {
         return new Short2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2ShortSortedMap tailMap(Short from) {
         return new Short2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
