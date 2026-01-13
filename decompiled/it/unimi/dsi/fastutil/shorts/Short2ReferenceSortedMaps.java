package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2ReferenceSortedMaps {
   public static final Short2ReferenceSortedMaps.EmptySortedMap EMPTY_MAP = new Short2ReferenceSortedMaps.EmptySortedMap();

   private Short2ReferenceSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static <V> ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> fastIterator(Short2ReferenceSortedMap<V> map) {
      ObjectSortedSet<Short2ReferenceMap.Entry<V>> entries = map.short2ReferenceEntrySet();
      return entries instanceof Short2ReferenceSortedMap.FastSortedEntrySet
         ? ((Short2ReferenceSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Short2ReferenceMap.Entry<V>> fastIterable(Short2ReferenceSortedMap<V> map) {
      ObjectSortedSet<Short2ReferenceMap.Entry<V>> entries = map.short2ReferenceEntrySet();
      return (ObjectBidirectionalIterable<Short2ReferenceMap.Entry<V>>)(entries instanceof Short2ReferenceSortedMap.FastSortedEntrySet
         ? ((Short2ReferenceSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <V> Short2ReferenceSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Short2ReferenceSortedMap<V> singleton(Short key, V value) {
      return new Short2ReferenceSortedMaps.Singleton<>(key, value);
   }

   public static <V> Short2ReferenceSortedMap<V> singleton(Short key, V value, ShortComparator comparator) {
      return new Short2ReferenceSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Short2ReferenceSortedMap<V> singleton(short key, V value) {
      return new Short2ReferenceSortedMaps.Singleton<>(key, value);
   }

   public static <V> Short2ReferenceSortedMap<V> singleton(short key, V value, ShortComparator comparator) {
      return new Short2ReferenceSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Short2ReferenceSortedMap<V> synchronize(Short2ReferenceSortedMap<V> m) {
      return new Short2ReferenceSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <V> Short2ReferenceSortedMap<V> synchronize(Short2ReferenceSortedMap<V> m, Object sync) {
      return new Short2ReferenceSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <V> Short2ReferenceSortedMap<V> unmodifiable(Short2ReferenceSortedMap<? extends V> m) {
      return new Short2ReferenceSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<V> extends Short2ReferenceMaps.EmptyMap<V> implements Short2ReferenceSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public Short2ReferenceSortedMap<V> subMap(short from, short to) {
         return Short2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2ReferenceSortedMap<V> headMap(short to) {
         return Short2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2ReferenceSortedMap<V> tailMap(short from) {
         return Short2ReferenceSortedMaps.EMPTY_MAP;
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
      public Short2ReferenceSortedMap<V> headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2ReferenceSortedMap<V> tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2ReferenceSortedMap<V> subMap(Short ofrom, Short oto) {
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

   public static class Singleton<V> extends Short2ReferenceMaps.Singleton<V> implements Short2ReferenceSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short key, V value, ShortComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(short key, V value) {
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
      public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2ReferenceMap.BasicEntry<>(this.key, this.value), Short2ReferenceSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ReferenceEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ReferenceSortedMap<V> subMap(short from, short to) {
         return (Short2ReferenceSortedMap<V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2ReferenceSortedMap<V> headMap(short to) {
         return (Short2ReferenceSortedMap<V>)(this.compare(this.key, to) < 0 ? this : Short2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2ReferenceSortedMap<V> tailMap(short from) {
         return (Short2ReferenceSortedMap<V>)(this.compare(from, this.key) <= 0 ? this : Short2ReferenceSortedMaps.EMPTY_MAP);
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
      public Short2ReferenceSortedMap<V> headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2ReferenceSortedMap<V> tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2ReferenceSortedMap<V> subMap(Short ofrom, Short oto) {
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

   public static class SynchronizedSortedMap<V> extends Short2ReferenceMaps.SynchronizedMap<V> implements Short2ReferenceSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ReferenceSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Short2ReferenceSortedMap<V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2ReferenceSortedMap<V> m) {
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
      public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2ReferenceEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ReferenceEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ReferenceSortedMap<V> subMap(short from, short to) {
         return new Short2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2ReferenceSortedMap<V> headMap(short to) {
         return new Short2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2ReferenceSortedMap<V> tailMap(short from) {
         return new Short2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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
      public Short2ReferenceSortedMap<V> subMap(Short from, Short to) {
         return new Short2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2ReferenceSortedMap<V> headMap(Short to) {
         return new Short2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2ReferenceSortedMap<V> tailMap(Short from) {
         return new Short2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap<V> extends Short2ReferenceMaps.UnmodifiableMap<V> implements Short2ReferenceSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ReferenceSortedMap<? extends V> sortedMap;

      protected UnmodifiableSortedMap(Short2ReferenceSortedMap<? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Short2ReferenceMap.Entry<V>>)this.sortedMap.short2ReferenceEntrySet());
         }

         return (ObjectSortedSet<Short2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ReferenceEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ReferenceSortedMap<V> subMap(short from, short to) {
         return new Short2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2ReferenceSortedMap<V> headMap(short to) {
         return new Short2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Short2ReferenceSortedMap<V> tailMap(short from) {
         return new Short2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
      public Short2ReferenceSortedMap<V> subMap(Short from, Short to) {
         return new Short2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2ReferenceSortedMap<V> headMap(Short to) {
         return new Short2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2ReferenceSortedMap<V> tailMap(Short from) {
         return new Short2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }
   }
}
