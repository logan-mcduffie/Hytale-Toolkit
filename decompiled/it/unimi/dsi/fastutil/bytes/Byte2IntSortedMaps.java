package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Byte2IntSortedMaps {
   public static final Byte2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2IntSortedMaps.EmptySortedMap();

   private Byte2IntSortedMaps() {
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().byteValue(), y.getKey().byteValue());
   }

   public static ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator(Byte2IntSortedMap map) {
      ObjectSortedSet<Byte2IntMap.Entry> entries = map.byte2IntEntrySet();
      return entries instanceof Byte2IntSortedMap.FastSortedEntrySet ? ((Byte2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2IntMap.Entry> fastIterable(Byte2IntSortedMap map) {
      ObjectSortedSet<Byte2IntMap.Entry> entries = map.byte2IntEntrySet();
      return (ObjectBidirectionalIterable<Byte2IntMap.Entry>)(entries instanceof Byte2IntSortedMap.FastSortedEntrySet
         ? ((Byte2IntSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Byte2IntSortedMap singleton(Byte key, Integer value) {
      return new Byte2IntSortedMaps.Singleton(key, value);
   }

   public static Byte2IntSortedMap singleton(Byte key, Integer value, ByteComparator comparator) {
      return new Byte2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2IntSortedMap singleton(byte key, int value) {
      return new Byte2IntSortedMaps.Singleton(key, value);
   }

   public static Byte2IntSortedMap singleton(byte key, int value, ByteComparator comparator) {
      return new Byte2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2IntSortedMap synchronize(Byte2IntSortedMap m) {
      return new Byte2IntSortedMaps.SynchronizedSortedMap(m);
   }

   public static Byte2IntSortedMap synchronize(Byte2IntSortedMap m, Object sync) {
      return new Byte2IntSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Byte2IntSortedMap unmodifiable(Byte2IntSortedMap m) {
      return new Byte2IntSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Byte2IntMaps.EmptyMap implements Byte2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      @Override
      public Byte2IntSortedMap subMap(byte from, byte to) {
         return Byte2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2IntSortedMap headMap(byte to) {
         return Byte2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2IntSortedMap tailMap(byte from) {
         return Byte2IntSortedMaps.EMPTY_MAP;
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
      public Byte2IntSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2IntSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2IntSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class Singleton extends Byte2IntMaps.Singleton implements Byte2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte key, int value, ByteComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(byte key, int value) {
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
      public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractByte2IntMap.BasicEntry(this.key, this.value), Byte2IntSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Byte2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2IntSortedMap subMap(byte from, byte to) {
         return (Byte2IntSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Byte2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2IntSortedMap headMap(byte to) {
         return (Byte2IntSortedMap)(this.compare(this.key, to) < 0 ? this : Byte2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2IntSortedMap tailMap(byte from) {
         return (Byte2IntSortedMap)(this.compare(from, this.key) <= 0 ? this : Byte2IntSortedMaps.EMPTY_MAP);
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
      public Byte2IntSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2IntSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2IntSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class SynchronizedSortedMap extends Byte2IntMaps.SynchronizedMap implements Byte2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2IntSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2IntSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Byte2IntSortedMap m) {
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
      public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Byte2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2IntSortedMap subMap(byte from, byte to) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Byte2IntSortedMap headMap(byte to) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Byte2IntSortedMap tailMap(byte from) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Byte2IntSortedMap subMap(Byte from, Byte to) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2IntSortedMap headMap(Byte to) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2IntSortedMap tailMap(Byte from) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Byte2IntMaps.UnmodifiableMap implements Byte2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2IntSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2IntSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2IntEntrySet());
         }

         return (ObjectSortedSet<Byte2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2IntSortedMap subMap(byte from, byte to) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Byte2IntSortedMap headMap(byte to) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Byte2IntSortedMap tailMap(byte from) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Byte2IntSortedMap subMap(Byte from, Byte to) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Byte2IntSortedMap headMap(Byte to) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Byte2IntSortedMap tailMap(Byte from) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
