package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2FloatSortedMaps {
   public static final Short2FloatSortedMaps.EmptySortedMap EMPTY_MAP = new Short2FloatSortedMaps.EmptySortedMap();

   private Short2FloatSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static ObjectBidirectionalIterator<Short2FloatMap.Entry> fastIterator(Short2FloatSortedMap map) {
      ObjectSortedSet<Short2FloatMap.Entry> entries = map.short2FloatEntrySet();
      return entries instanceof Short2FloatSortedMap.FastSortedEntrySet
         ? ((Short2FloatSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Short2FloatMap.Entry> fastIterable(Short2FloatSortedMap map) {
      ObjectSortedSet<Short2FloatMap.Entry> entries = map.short2FloatEntrySet();
      return (ObjectBidirectionalIterable<Short2FloatMap.Entry>)(entries instanceof Short2FloatSortedMap.FastSortedEntrySet
         ? ((Short2FloatSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Short2FloatSortedMap singleton(Short key, Float value) {
      return new Short2FloatSortedMaps.Singleton(key, value);
   }

   public static Short2FloatSortedMap singleton(Short key, Float value, ShortComparator comparator) {
      return new Short2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2FloatSortedMap singleton(short key, float value) {
      return new Short2FloatSortedMaps.Singleton(key, value);
   }

   public static Short2FloatSortedMap singleton(short key, float value, ShortComparator comparator) {
      return new Short2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2FloatSortedMap synchronize(Short2FloatSortedMap m) {
      return new Short2FloatSortedMaps.SynchronizedSortedMap(m);
   }

   public static Short2FloatSortedMap synchronize(Short2FloatSortedMap m, Object sync) {
      return new Short2FloatSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Short2FloatSortedMap unmodifiable(Short2FloatSortedMap m) {
      return new Short2FloatSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Short2FloatMaps.EmptyMap implements Short2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2FloatMap.Entry> short2FloatEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Float>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public Short2FloatSortedMap subMap(short from, short to) {
         return Short2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2FloatSortedMap headMap(short to) {
         return Short2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2FloatSortedMap tailMap(short from) {
         return Short2FloatSortedMaps.EMPTY_MAP;
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
      public Short2FloatSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2FloatSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2FloatSortedMap subMap(Short ofrom, Short oto) {
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

   public static class Singleton extends Short2FloatMaps.Singleton implements Short2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short key, float value, ShortComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(short key, float value) {
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
      public ObjectSortedSet<Short2FloatMap.Entry> short2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2FloatMap.BasicEntry(this.key, this.value), Short2FloatSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Float>> entrySet() {
         return this.short2FloatEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2FloatSortedMap subMap(short from, short to) {
         return (Short2FloatSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2FloatSortedMap headMap(short to) {
         return (Short2FloatSortedMap)(this.compare(this.key, to) < 0 ? this : Short2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2FloatSortedMap tailMap(short from) {
         return (Short2FloatSortedMap)(this.compare(from, this.key) <= 0 ? this : Short2FloatSortedMaps.EMPTY_MAP);
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
      public Short2FloatSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2FloatSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2FloatSortedMap subMap(Short ofrom, Short oto) {
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

   public static class SynchronizedSortedMap extends Short2FloatMaps.SynchronizedMap implements Short2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2FloatSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2FloatSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2FloatSortedMap m) {
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
      public ObjectSortedSet<Short2FloatMap.Entry> short2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2FloatEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Float>> entrySet() {
         return this.short2FloatEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2FloatSortedMap subMap(short from, short to) {
         return new Short2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2FloatSortedMap headMap(short to) {
         return new Short2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2FloatSortedMap tailMap(short from) {
         return new Short2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Short2FloatSortedMap subMap(Short from, Short to) {
         return new Short2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2FloatSortedMap headMap(Short to) {
         return new Short2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2FloatSortedMap tailMap(Short from) {
         return new Short2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Short2FloatMaps.UnmodifiableMap implements Short2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2FloatSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2FloatSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2FloatMap.Entry> short2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2FloatEntrySet());
         }

         return (ObjectSortedSet<Short2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Float>> entrySet() {
         return this.short2FloatEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2FloatSortedMap subMap(short from, short to) {
         return new Short2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2FloatSortedMap headMap(short to) {
         return new Short2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Short2FloatSortedMap tailMap(short from) {
         return new Short2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Short2FloatSortedMap subMap(Short from, Short to) {
         return new Short2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2FloatSortedMap headMap(Short to) {
         return new Short2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2FloatSortedMap tailMap(Short from) {
         return new Short2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
