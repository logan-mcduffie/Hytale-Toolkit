package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Byte2ByteSortedMaps {
   public static final Byte2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2ByteSortedMaps.EmptySortedMap();

   private Byte2ByteSortedMaps() {
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().byteValue(), y.getKey().byteValue());
   }

   public static ObjectBidirectionalIterator<Byte2ByteMap.Entry> fastIterator(Byte2ByteSortedMap map) {
      ObjectSortedSet<Byte2ByteMap.Entry> entries = map.byte2ByteEntrySet();
      return entries instanceof Byte2ByteSortedMap.FastSortedEntrySet ? ((Byte2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2ByteMap.Entry> fastIterable(Byte2ByteSortedMap map) {
      ObjectSortedSet<Byte2ByteMap.Entry> entries = map.byte2ByteEntrySet();
      return (ObjectBidirectionalIterable<Byte2ByteMap.Entry>)(entries instanceof Byte2ByteSortedMap.FastSortedEntrySet
         ? ((Byte2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Byte2ByteSortedMap singleton(Byte key, Byte value) {
      return new Byte2ByteSortedMaps.Singleton(key, value);
   }

   public static Byte2ByteSortedMap singleton(Byte key, Byte value, ByteComparator comparator) {
      return new Byte2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2ByteSortedMap singleton(byte key, byte value) {
      return new Byte2ByteSortedMaps.Singleton(key, value);
   }

   public static Byte2ByteSortedMap singleton(byte key, byte value, ByteComparator comparator) {
      return new Byte2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2ByteSortedMap synchronize(Byte2ByteSortedMap m) {
      return new Byte2ByteSortedMaps.SynchronizedSortedMap(m);
   }

   public static Byte2ByteSortedMap synchronize(Byte2ByteSortedMap m, Object sync) {
      return new Byte2ByteSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Byte2ByteSortedMap unmodifiable(Byte2ByteSortedMap m) {
      return new Byte2ByteSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Byte2ByteMaps.EmptyMap implements Byte2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Byte2ByteMap.Entry> byte2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      @Override
      public Byte2ByteSortedMap subMap(byte from, byte to) {
         return Byte2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2ByteSortedMap headMap(byte to) {
         return Byte2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2ByteSortedMap tailMap(byte from) {
         return Byte2ByteSortedMaps.EMPTY_MAP;
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
      public Byte2ByteSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ByteSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ByteSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class Singleton extends Byte2ByteMaps.Singleton implements Byte2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte key, byte value, ByteComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(byte key, byte value) {
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
      public ObjectSortedSet<Byte2ByteMap.Entry> byte2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractByte2ByteMap.BasicEntry(this.key, this.value), Byte2ByteSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Byte2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Byte>> entrySet() {
         return this.byte2ByteEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ByteSortedMap subMap(byte from, byte to) {
         return (Byte2ByteSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Byte2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2ByteSortedMap headMap(byte to) {
         return (Byte2ByteSortedMap)(this.compare(this.key, to) < 0 ? this : Byte2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2ByteSortedMap tailMap(byte from) {
         return (Byte2ByteSortedMap)(this.compare(from, this.key) <= 0 ? this : Byte2ByteSortedMaps.EMPTY_MAP);
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
      public Byte2ByteSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ByteSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2ByteSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class SynchronizedSortedMap extends Byte2ByteMaps.SynchronizedMap implements Byte2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ByteSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2ByteSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Byte2ByteSortedMap m) {
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
      public ObjectSortedSet<Byte2ByteMap.Entry> byte2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Byte2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Byte>> entrySet() {
         return this.byte2ByteEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ByteSortedMap subMap(byte from, byte to) {
         return new Byte2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Byte2ByteSortedMap headMap(byte to) {
         return new Byte2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Byte2ByteSortedMap tailMap(byte from) {
         return new Byte2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Byte2ByteSortedMap subMap(Byte from, Byte to) {
         return new Byte2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2ByteSortedMap headMap(Byte to) {
         return new Byte2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2ByteSortedMap tailMap(Byte from) {
         return new Byte2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Byte2ByteMaps.UnmodifiableMap implements Byte2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ByteSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2ByteSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Byte2ByteMap.Entry> byte2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2ByteEntrySet());
         }

         return (ObjectSortedSet<Byte2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Byte>> entrySet() {
         return this.byte2ByteEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2ByteSortedMap subMap(byte from, byte to) {
         return new Byte2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Byte2ByteSortedMap headMap(byte to) {
         return new Byte2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Byte2ByteSortedMap tailMap(byte from) {
         return new Byte2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Byte2ByteSortedMap subMap(Byte from, Byte to) {
         return new Byte2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Byte2ByteSortedMap headMap(Byte to) {
         return new Byte2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Byte2ByteSortedMap tailMap(Byte from) {
         return new Byte2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
