package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2BooleanSortedMaps {
   public static final Short2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Short2BooleanSortedMaps.EmptySortedMap();

   private Short2BooleanSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static ObjectBidirectionalIterator<Short2BooleanMap.Entry> fastIterator(Short2BooleanSortedMap map) {
      ObjectSortedSet<Short2BooleanMap.Entry> entries = map.short2BooleanEntrySet();
      return entries instanceof Short2BooleanSortedMap.FastSortedEntrySet
         ? ((Short2BooleanSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Short2BooleanMap.Entry> fastIterable(Short2BooleanSortedMap map) {
      ObjectSortedSet<Short2BooleanMap.Entry> entries = map.short2BooleanEntrySet();
      return (ObjectBidirectionalIterable<Short2BooleanMap.Entry>)(entries instanceof Short2BooleanSortedMap.FastSortedEntrySet
         ? ((Short2BooleanSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Short2BooleanSortedMap singleton(Short key, Boolean value) {
      return new Short2BooleanSortedMaps.Singleton(key, value);
   }

   public static Short2BooleanSortedMap singleton(Short key, Boolean value, ShortComparator comparator) {
      return new Short2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2BooleanSortedMap singleton(short key, boolean value) {
      return new Short2BooleanSortedMaps.Singleton(key, value);
   }

   public static Short2BooleanSortedMap singleton(short key, boolean value, ShortComparator comparator) {
      return new Short2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2BooleanSortedMap synchronize(Short2BooleanSortedMap m) {
      return new Short2BooleanSortedMaps.SynchronizedSortedMap(m);
   }

   public static Short2BooleanSortedMap synchronize(Short2BooleanSortedMap m, Object sync) {
      return new Short2BooleanSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Short2BooleanSortedMap unmodifiable(Short2BooleanSortedMap m) {
      return new Short2BooleanSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Short2BooleanMaps.EmptyMap implements Short2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public Short2BooleanSortedMap subMap(short from, short to) {
         return Short2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2BooleanSortedMap headMap(short to) {
         return Short2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2BooleanSortedMap tailMap(short from) {
         return Short2BooleanSortedMaps.EMPTY_MAP;
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
      public Short2BooleanSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2BooleanSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2BooleanSortedMap subMap(Short ofrom, Short oto) {
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

   public static class Singleton extends Short2BooleanMaps.Singleton implements Short2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short key, boolean value, ShortComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(short key, boolean value) {
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
      public ObjectSortedSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2BooleanMap.BasicEntry(this.key, this.value), Short2BooleanSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2BooleanSortedMap subMap(short from, short to) {
         return (Short2BooleanSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2BooleanSortedMap headMap(short to) {
         return (Short2BooleanSortedMap)(this.compare(this.key, to) < 0 ? this : Short2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2BooleanSortedMap tailMap(short from) {
         return (Short2BooleanSortedMap)(this.compare(from, this.key) <= 0 ? this : Short2BooleanSortedMaps.EMPTY_MAP);
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
      public Short2BooleanSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2BooleanSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2BooleanSortedMap subMap(Short ofrom, Short oto) {
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

   public static class SynchronizedSortedMap extends Short2BooleanMaps.SynchronizedMap implements Short2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2BooleanSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2BooleanSortedMap m) {
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
      public ObjectSortedSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2BooleanSortedMap subMap(short from, short to) {
         return new Short2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2BooleanSortedMap headMap(short to) {
         return new Short2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2BooleanSortedMap tailMap(short from) {
         return new Short2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Short2BooleanSortedMap subMap(Short from, Short to) {
         return new Short2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2BooleanSortedMap headMap(Short to) {
         return new Short2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2BooleanSortedMap tailMap(Short from) {
         return new Short2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Short2BooleanMaps.UnmodifiableMap implements Short2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2BooleanSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2BooleanMap.Entry> short2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2BooleanEntrySet());
         }

         return (ObjectSortedSet<Short2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Boolean>> entrySet() {
         return this.short2BooleanEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2BooleanSortedMap subMap(short from, short to) {
         return new Short2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2BooleanSortedMap headMap(short to) {
         return new Short2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Short2BooleanSortedMap tailMap(short from) {
         return new Short2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Short2BooleanSortedMap subMap(Short from, Short to) {
         return new Short2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2BooleanSortedMap headMap(Short to) {
         return new Short2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2BooleanSortedMap tailMap(Short from) {
         return new Short2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
