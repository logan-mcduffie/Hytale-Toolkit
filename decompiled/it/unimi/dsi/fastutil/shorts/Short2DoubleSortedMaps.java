package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2DoubleSortedMaps {
   public static final Short2DoubleSortedMaps.EmptySortedMap EMPTY_MAP = new Short2DoubleSortedMaps.EmptySortedMap();

   private Short2DoubleSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static ObjectBidirectionalIterator<Short2DoubleMap.Entry> fastIterator(Short2DoubleSortedMap map) {
      ObjectSortedSet<Short2DoubleMap.Entry> entries = map.short2DoubleEntrySet();
      return entries instanceof Short2DoubleSortedMap.FastSortedEntrySet
         ? ((Short2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Short2DoubleMap.Entry> fastIterable(Short2DoubleSortedMap map) {
      ObjectSortedSet<Short2DoubleMap.Entry> entries = map.short2DoubleEntrySet();
      return (ObjectBidirectionalIterable<Short2DoubleMap.Entry>)(entries instanceof Short2DoubleSortedMap.FastSortedEntrySet
         ? ((Short2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Short2DoubleSortedMap singleton(Short key, Double value) {
      return new Short2DoubleSortedMaps.Singleton(key, value);
   }

   public static Short2DoubleSortedMap singleton(Short key, Double value, ShortComparator comparator) {
      return new Short2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2DoubleSortedMap singleton(short key, double value) {
      return new Short2DoubleSortedMaps.Singleton(key, value);
   }

   public static Short2DoubleSortedMap singleton(short key, double value, ShortComparator comparator) {
      return new Short2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2DoubleSortedMap synchronize(Short2DoubleSortedMap m) {
      return new Short2DoubleSortedMaps.SynchronizedSortedMap(m);
   }

   public static Short2DoubleSortedMap synchronize(Short2DoubleSortedMap m, Object sync) {
      return new Short2DoubleSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Short2DoubleSortedMap unmodifiable(Short2DoubleSortedMap m) {
      return new Short2DoubleSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Short2DoubleMaps.EmptyMap implements Short2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Double>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public Short2DoubleSortedMap subMap(short from, short to) {
         return Short2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2DoubleSortedMap headMap(short to) {
         return Short2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2DoubleSortedMap tailMap(short from) {
         return Short2DoubleSortedMaps.EMPTY_MAP;
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
      public Short2DoubleSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2DoubleSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2DoubleSortedMap subMap(Short ofrom, Short oto) {
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

   public static class Singleton extends Short2DoubleMaps.Singleton implements Short2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short key, double value, ShortComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(short key, double value) {
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
      public ObjectSortedSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2DoubleMap.BasicEntry(this.key, this.value), Short2DoubleSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Double>> entrySet() {
         return this.short2DoubleEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2DoubleSortedMap subMap(short from, short to) {
         return (Short2DoubleSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2DoubleSortedMap headMap(short to) {
         return (Short2DoubleSortedMap)(this.compare(this.key, to) < 0 ? this : Short2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2DoubleSortedMap tailMap(short from) {
         return (Short2DoubleSortedMap)(this.compare(from, this.key) <= 0 ? this : Short2DoubleSortedMaps.EMPTY_MAP);
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
      public Short2DoubleSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2DoubleSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2DoubleSortedMap subMap(Short ofrom, Short oto) {
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

   public static class SynchronizedSortedMap extends Short2DoubleMaps.SynchronizedMap implements Short2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2DoubleSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2DoubleSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2DoubleSortedMap m) {
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
      public ObjectSortedSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2DoubleEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Double>> entrySet() {
         return this.short2DoubleEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2DoubleSortedMap subMap(short from, short to) {
         return new Short2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2DoubleSortedMap headMap(short to) {
         return new Short2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2DoubleSortedMap tailMap(short from) {
         return new Short2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Short2DoubleSortedMap subMap(Short from, Short to) {
         return new Short2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2DoubleSortedMap headMap(Short to) {
         return new Short2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2DoubleSortedMap tailMap(Short from) {
         return new Short2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Short2DoubleMaps.UnmodifiableMap implements Short2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2DoubleSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2DoubleEntrySet());
         }

         return (ObjectSortedSet<Short2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Double>> entrySet() {
         return this.short2DoubleEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2DoubleSortedMap subMap(short from, short to) {
         return new Short2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2DoubleSortedMap headMap(short to) {
         return new Short2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Short2DoubleSortedMap tailMap(short from) {
         return new Short2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Short2DoubleSortedMap subMap(Short from, Short to) {
         return new Short2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2DoubleSortedMap headMap(Short to) {
         return new Short2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2DoubleSortedMap tailMap(Short from) {
         return new Short2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
