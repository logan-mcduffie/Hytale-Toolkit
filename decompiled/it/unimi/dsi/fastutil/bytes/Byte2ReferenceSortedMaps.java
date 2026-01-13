package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Byte2ReferenceSortedMaps {
   public static final Byte2ReferenceSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2ReferenceSortedMaps.EmptySortedMap();

   private Byte2ReferenceSortedMaps() {
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().byteValue(), y.getKey().byteValue());
   }

   public static <V> ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> fastIterator(Byte2ReferenceSortedMap<V> map) {
      ObjectSortedSet<Byte2ReferenceMap.Entry<V>> entries = map.byte2ReferenceEntrySet();
      return entries instanceof Byte2ReferenceSortedMap.FastSortedEntrySet
         ? ((Byte2ReferenceSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Byte2ReferenceMap.Entry<V>> fastIterable(Byte2ReferenceSortedMap<V> map) {
      ObjectSortedSet<Byte2ReferenceMap.Entry<V>> entries = map.byte2ReferenceEntrySet();
      return (ObjectBidirectionalIterable<Byte2ReferenceMap.Entry<V>>)(entries instanceof Byte2ReferenceSortedMap.FastSortedEntrySet
         ? ((Byte2ReferenceSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <V> Byte2ReferenceSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Byte2ReferenceSortedMap<V> singleton(Byte key, V value) {
      return new Byte2ReferenceSortedMaps.Singleton<>(key, value);
   }

   public static <V> Byte2ReferenceSortedMap<V> singleton(Byte key, V value, ByteComparator comparator) {
      return new Byte2ReferenceSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Byte2ReferenceSortedMap<V> singleton(byte key, V value) {
      return new Byte2ReferenceSortedMaps.Singleton<>(key, value);
   }

   public static <V> Byte2ReferenceSortedMap<V> singleton(byte key, V value, ByteComparator comparator) {
      return new Byte2ReferenceSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Byte2ReferenceSortedMap<V> synchronize(Byte2ReferenceSortedMap<V> m) {
      return new Byte2ReferenceSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <V> Byte2ReferenceSortedMap<V> synchronize(Byte2ReferenceSortedMap<V> m, Object sync) {
      return new Byte2ReferenceSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <V> Byte2ReferenceSortedMap<V> unmodifiable(Byte2ReferenceSortedMap<? extends V> m) {
      return new Byte2ReferenceSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<V> extends Byte2ReferenceMaps.EmptyMap<V> implements Byte2ReferenceSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      @Override
      public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
         return Byte2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2ReferenceSortedMap<V> headMap(byte to) {
         return Byte2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2ReferenceSortedMap<V> tailMap(byte from) {
         return Byte2ReferenceSortedMaps.EMPTY_MAP;
      }

      @Override
      public byte firstByteKey() {
         throw new NoSuchElementException();
      }

      @Override
      public byte lastByteKey() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> subMap(Byte ofrom, Byte oto) {
         return this.subMap(ofrom.byteValue(), oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte firstKey() {
         return this.firstByteKey();
      }

      @Deprecated
      @Override
      public Byte lastKey() {
         return this.lastByteKey();
      }
   }

   public static class Singleton<V> extends Byte2ReferenceMaps.Singleton<V> implements Byte2ReferenceSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte key, V value, ByteComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(byte key, V value) {
         this(key, value, null);
      }

      final int compare(byte k1, byte k2) {
         return this.comparator == null ? Byte.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public ByteComparator comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractByte2ReferenceMap.BasicEntry<>(this.key, this.value), Byte2ReferenceSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Byte2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ReferenceEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
         return (Byte2ReferenceSortedMap<V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Byte2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2ReferenceSortedMap<V> headMap(byte to) {
         return (Byte2ReferenceSortedMap<V>)(this.compare(this.key, to) < 0 ? this : Byte2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2ReferenceSortedMap<V> tailMap(byte from) {
         return (Byte2ReferenceSortedMap<V>)(this.compare(from, this.key) <= 0 ? this : Byte2ReferenceSortedMaps.EMPTY_MAP);
      }

      @Override
      public byte firstByteKey() {
         return this.key;
      }

      @Override
      public byte lastByteKey() {
         return this.key;
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> subMap(Byte ofrom, Byte oto) {
         return this.subMap(ofrom.byteValue(), oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte firstKey() {
         return this.firstByteKey();
      }

      @Deprecated
      @Override
      public Byte lastKey() {
         return this.lastByteKey();
      }
   }

   public static class SynchronizedSortedMap<V> extends Byte2ReferenceMaps.SynchronizedMap<V> implements Byte2ReferenceSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ReferenceSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Byte2ReferenceSortedMap<V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Byte2ReferenceSortedMap<V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         synchronized (this.sync) {
            return this.sortedMap.comparator();
         }
      }

      @Override
      public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2ReferenceEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Byte2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ReferenceEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
         return new Byte2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Byte2ReferenceSortedMap<V> headMap(byte to) {
         return new Byte2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Byte2ReferenceSortedMap<V> tailMap(byte from) {
         return new Byte2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }

      @Override
      public byte firstByteKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstByteKey();
         }
      }

      @Override
      public byte lastByteKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastByteKey();
         }
      }

      @Deprecated
      @Override
      public Byte firstKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      @Deprecated
      @Override
      public Byte lastKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> subMap(Byte from, Byte to) {
         return new Byte2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> headMap(Byte to) {
         return new Byte2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> tailMap(Byte from) {
         return new Byte2ReferenceSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap<V> extends Byte2ReferenceMaps.UnmodifiableMap<V> implements Byte2ReferenceSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ReferenceSortedMap<? extends V> sortedMap;

      protected UnmodifiableSortedMap(Byte2ReferenceSortedMap<? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Byte2ReferenceMap.Entry<V>>)this.sortedMap.byte2ReferenceEntrySet());
         }

         return (ObjectSortedSet<Byte2ReferenceMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ReferenceEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
         return new Byte2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Byte2ReferenceSortedMap<V> headMap(byte to) {
         return new Byte2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Byte2ReferenceSortedMap<V> tailMap(byte from) {
         return new Byte2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }

      @Override
      public byte firstByteKey() {
         return this.sortedMap.firstByteKey();
      }

      @Override
      public byte lastByteKey() {
         return this.sortedMap.lastByteKey();
      }

      @Deprecated
      @Override
      public Byte firstKey() {
         return this.sortedMap.firstKey();
      }

      @Deprecated
      @Override
      public Byte lastKey() {
         return this.sortedMap.lastKey();
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> subMap(Byte from, Byte to) {
         return new Byte2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> headMap(Byte to) {
         return new Byte2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Byte2ReferenceSortedMap<V> tailMap(Byte from) {
         return new Byte2ReferenceSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }
   }
}
