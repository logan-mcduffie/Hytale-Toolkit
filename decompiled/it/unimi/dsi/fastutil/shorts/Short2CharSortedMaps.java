package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2CharSortedMaps {
   public static final Short2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Short2CharSortedMaps.EmptySortedMap();

   private Short2CharSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static ObjectBidirectionalIterator<Short2CharMap.Entry> fastIterator(Short2CharSortedMap map) {
      ObjectSortedSet<Short2CharMap.Entry> entries = map.short2CharEntrySet();
      return entries instanceof Short2CharSortedMap.FastSortedEntrySet ? ((Short2CharSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Short2CharMap.Entry> fastIterable(Short2CharSortedMap map) {
      ObjectSortedSet<Short2CharMap.Entry> entries = map.short2CharEntrySet();
      return (ObjectBidirectionalIterable<Short2CharMap.Entry>)(entries instanceof Short2CharSortedMap.FastSortedEntrySet
         ? ((Short2CharSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Short2CharSortedMap singleton(Short key, Character value) {
      return new Short2CharSortedMaps.Singleton(key, value);
   }

   public static Short2CharSortedMap singleton(Short key, Character value, ShortComparator comparator) {
      return new Short2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2CharSortedMap singleton(short key, char value) {
      return new Short2CharSortedMaps.Singleton(key, value);
   }

   public static Short2CharSortedMap singleton(short key, char value, ShortComparator comparator) {
      return new Short2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Short2CharSortedMap synchronize(Short2CharSortedMap m) {
      return new Short2CharSortedMaps.SynchronizedSortedMap(m);
   }

   public static Short2CharSortedMap synchronize(Short2CharSortedMap m, Object sync) {
      return new Short2CharSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Short2CharSortedMap unmodifiable(Short2CharSortedMap m) {
      return new Short2CharSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Short2CharMaps.EmptyMap implements Short2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public Short2CharSortedMap subMap(short from, short to) {
         return Short2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2CharSortedMap headMap(short to) {
         return Short2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2CharSortedMap tailMap(short from) {
         return Short2CharSortedMaps.EMPTY_MAP;
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
      public Short2CharSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2CharSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2CharSortedMap subMap(Short ofrom, Short oto) {
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

   public static class Singleton extends Short2CharMaps.Singleton implements Short2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short key, char value, ShortComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(short key, char value) {
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
      public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2CharMap.BasicEntry(this.key, this.value), Short2CharSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2CharSortedMap subMap(short from, short to) {
         return (Short2CharSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2CharSortedMap headMap(short to) {
         return (Short2CharSortedMap)(this.compare(this.key, to) < 0 ? this : Short2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2CharSortedMap tailMap(short from) {
         return (Short2CharSortedMap)(this.compare(from, this.key) <= 0 ? this : Short2CharSortedMaps.EMPTY_MAP);
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
      public Short2CharSortedMap headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2CharSortedMap tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2CharSortedMap subMap(Short ofrom, Short oto) {
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

   public static class SynchronizedSortedMap extends Short2CharMaps.SynchronizedMap implements Short2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2CharSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2CharSortedMap m) {
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
      public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2CharSortedMap subMap(short from, short to) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2CharSortedMap headMap(short to) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2CharSortedMap tailMap(short from) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Short2CharSortedMap subMap(Short from, Short to) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2CharSortedMap headMap(Short to) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2CharSortedMap tailMap(Short from) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Short2CharMaps.UnmodifiableMap implements Short2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2CharSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2CharEntrySet());
         }

         return (ObjectSortedSet<Short2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2CharSortedMap subMap(short from, short to) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2CharSortedMap headMap(short to) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Short2CharSortedMap tailMap(short from) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Short2CharSortedMap subMap(Short from, Short to) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2CharSortedMap headMap(Short to) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2CharSortedMap tailMap(Short from) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
