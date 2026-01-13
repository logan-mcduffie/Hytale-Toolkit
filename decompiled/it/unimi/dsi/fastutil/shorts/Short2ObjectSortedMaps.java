package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Short2ObjectSortedMaps {
   public static final Short2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Short2ObjectSortedMaps.EmptySortedMap();

   private Short2ObjectSortedMaps() {
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().shortValue(), y.getKey().shortValue());
   }

   public static <V> ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> fastIterator(Short2ObjectSortedMap<V> map) {
      ObjectSortedSet<Short2ObjectMap.Entry<V>> entries = map.short2ObjectEntrySet();
      return entries instanceof Short2ObjectSortedMap.FastSortedEntrySet
         ? ((Short2ObjectSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Short2ObjectMap.Entry<V>> fastIterable(Short2ObjectSortedMap<V> map) {
      ObjectSortedSet<Short2ObjectMap.Entry<V>> entries = map.short2ObjectEntrySet();
      return (ObjectBidirectionalIterable<Short2ObjectMap.Entry<V>>)(entries instanceof Short2ObjectSortedMap.FastSortedEntrySet
         ? ((Short2ObjectSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <V> Short2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Short2ObjectSortedMap<V> singleton(Short key, V value) {
      return new Short2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Short2ObjectSortedMap<V> singleton(Short key, V value, ShortComparator comparator) {
      return new Short2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Short2ObjectSortedMap<V> singleton(short key, V value) {
      return new Short2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Short2ObjectSortedMap<V> singleton(short key, V value, ShortComparator comparator) {
      return new Short2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Short2ObjectSortedMap<V> synchronize(Short2ObjectSortedMap<V> m) {
      return new Short2ObjectSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <V> Short2ObjectSortedMap<V> synchronize(Short2ObjectSortedMap<V> m, Object sync) {
      return new Short2ObjectSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <V> Short2ObjectSortedMap<V> unmodifiable(Short2ObjectSortedMap<? extends V> m) {
      return new Short2ObjectSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<V> extends Short2ObjectMaps.EmptyMap<V> implements Short2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
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
      public Short2ObjectSortedMap<V> subMap(short from, short to) {
         return Short2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2ObjectSortedMap<V> headMap(short to) {
         return Short2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Short2ObjectSortedMap<V> tailMap(short from) {
         return Short2ObjectSortedMaps.EMPTY_MAP;
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
      public Short2ObjectSortedMap<V> headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2ObjectSortedMap<V> tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2ObjectSortedMap<V> subMap(Short ofrom, Short oto) {
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

   public static class Singleton<V> extends Short2ObjectMaps.Singleton<V> implements Short2ObjectSortedMap<V>, Serializable, Cloneable {
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
      public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractShort2ObjectMap.BasicEntry<>(this.key, this.value), Short2ObjectSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Short2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ObjectEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ObjectSortedMap<V> subMap(short from, short to) {
         return (Short2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Short2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2ObjectSortedMap<V> headMap(short to) {
         return (Short2ObjectSortedMap<V>)(this.compare(this.key, to) < 0 ? this : Short2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Short2ObjectSortedMap<V> tailMap(short from) {
         return (Short2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 ? this : Short2ObjectSortedMaps.EMPTY_MAP);
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
      public Short2ObjectSortedMap<V> headMap(Short oto) {
         return this.headMap(oto.shortValue());
      }

      @Deprecated
      @Override
      public Short2ObjectSortedMap<V> tailMap(Short ofrom) {
         return this.tailMap(ofrom.shortValue());
      }

      @Deprecated
      @Override
      public Short2ObjectSortedMap<V> subMap(Short ofrom, Short oto) {
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

   public static class SynchronizedSortedMap<V> extends Short2ObjectMaps.SynchronizedMap<V> implements Short2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Short2ObjectSortedMap<V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Short2ObjectSortedMap<V> m) {
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
      public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Short2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ObjectEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ObjectSortedMap<V> subMap(short from, short to) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Short2ObjectSortedMap<V> headMap(short to) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Short2ObjectSortedMap<V> tailMap(short from) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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
      public Short2ObjectSortedMap<V> subMap(Short from, Short to) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Short2ObjectSortedMap<V> headMap(Short to) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Short2ObjectSortedMap<V> tailMap(Short from) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap<V> extends Short2ObjectMaps.UnmodifiableMap<V> implements Short2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ObjectSortedMap<? extends V> sortedMap;

      protected UnmodifiableSortedMap(Short2ObjectSortedMap<? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Short2ObjectMap.Entry<V>>)this.sortedMap.short2ObjectEntrySet());
         }

         return (ObjectSortedSet<Short2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ObjectEntrySet();
      }

      @Override
      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      @Override
      public Short2ObjectSortedMap<V> subMap(short from, short to) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Short2ObjectSortedMap<V> headMap(short to) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Short2ObjectSortedMap<V> tailMap(short from) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
      public Short2ObjectSortedMap<V> subMap(Short from, Short to) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Short2ObjectSortedMap<V> headMap(Short to) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Short2ObjectSortedMap<V> tailMap(Short from) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }
   }
}
