package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Byte2ObjectSortedMaps {
   public static final Byte2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2ObjectSortedMaps.EmptySortedMap();

   private Byte2ObjectSortedMaps() {
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().byteValue(), y.getKey().byteValue());
   }

   public static <V> ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> fastIterator(Byte2ObjectSortedMap<V> map) {
      ObjectSortedSet<Byte2ObjectMap.Entry<V>> entries = map.byte2ObjectEntrySet();
      return entries instanceof Byte2ObjectSortedMap.FastSortedEntrySet
         ? ((Byte2ObjectSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Byte2ObjectMap.Entry<V>> fastIterable(Byte2ObjectSortedMap<V> map) {
      ObjectSortedSet<Byte2ObjectMap.Entry<V>> entries = map.byte2ObjectEntrySet();
      return (ObjectBidirectionalIterable<Byte2ObjectMap.Entry<V>>)(entries instanceof Byte2ObjectSortedMap.FastSortedEntrySet
         ? ((Byte2ObjectSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <V> Byte2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Byte2ObjectSortedMap<V> singleton(Byte key, V value) {
      return new Byte2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Byte2ObjectSortedMap<V> singleton(Byte key, V value, ByteComparator comparator) {
      return new Byte2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Byte2ObjectSortedMap<V> singleton(byte key, V value) {
      return new Byte2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Byte2ObjectSortedMap<V> singleton(byte key, V value, ByteComparator comparator) {
      return new Byte2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Byte2ObjectSortedMap<V> synchronize(Byte2ObjectSortedMap<V> m) {
      return new Byte2ObjectSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <V> Byte2ObjectSortedMap<V> synchronize(Byte2ObjectSortedMap<V> m, Object sync) {
      return new Byte2ObjectSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <V> Byte2ObjectSortedMap<V> unmodifiable(Byte2ObjectSortedMap<? extends V> m) {
      return new Byte2ObjectSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<V> extends Byte2ObjectMaps.EmptyMap<V> implements Byte2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
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
      public Byte2ObjectSortedMap<V> subMap(byte from, byte to) {
         return Byte2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2ObjectSortedMap<V> headMap(byte to) {
         return Byte2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2ObjectSortedMap<V> tailMap(byte from) {
         return Byte2ObjectSortedMaps.EMPTY_MAP;
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
      public Byte2ObjectSortedMap<V> headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ObjectSortedMap<V> tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ObjectSortedMap<V> subMap(Byte ofrom, Byte oto) {
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

   public static class Singleton<V> extends Byte2ObjectMaps.Singleton<V> implements Byte2ObjectSortedMap<V>, Serializable, Cloneable {
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
      public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractByte2ObjectMap.BasicEntry<>(this.key, this.value), Byte2ObjectSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Byte2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ObjectSortedMap<V> subMap(byte from, byte to) {
         return (Byte2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Byte2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2ObjectSortedMap<V> headMap(byte to) {
         return (Byte2ObjectSortedMap<V>)(this.compare(this.key, to) < 0 ? this : Byte2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2ObjectSortedMap<V> tailMap(byte from) {
         return (Byte2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 ? this : Byte2ObjectSortedMaps.EMPTY_MAP);
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
      public Byte2ObjectSortedMap<V> headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ObjectSortedMap<V> tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ObjectSortedMap<V> subMap(Byte ofrom, Byte oto) {
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

   public static class SynchronizedSortedMap<V> extends Byte2ObjectMaps.SynchronizedMap<V> implements Byte2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Byte2ObjectSortedMap<V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Byte2ObjectSortedMap<V> m) {
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
      public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Byte2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ObjectSortedMap<V> subMap(byte from, byte to) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Byte2ObjectSortedMap<V> headMap(byte to) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Byte2ObjectSortedMap<V> tailMap(byte from) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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
      public Byte2ObjectSortedMap<V> subMap(Byte from, Byte to) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2ObjectSortedMap<V> headMap(Byte to) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2ObjectSortedMap<V> tailMap(Byte from) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap<V> extends Byte2ObjectMaps.UnmodifiableMap<V> implements Byte2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectSortedMap<? extends V> sortedMap;

      protected UnmodifiableSortedMap(Byte2ObjectSortedMap<? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Byte2ObjectMap.Entry<V>>)this.sortedMap.byte2ObjectEntrySet());
         }

         return (ObjectSortedSet<Byte2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ObjectSortedMap<V> subMap(byte from, byte to) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Byte2ObjectSortedMap<V> headMap(byte to) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Byte2ObjectSortedMap<V> tailMap(byte from) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
      public Byte2ObjectSortedMap<V> subMap(Byte from, Byte to) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Byte2ObjectSortedMap<V> headMap(Byte to) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Byte2ObjectSortedMap<V> tailMap(Byte from) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }
   }
}
