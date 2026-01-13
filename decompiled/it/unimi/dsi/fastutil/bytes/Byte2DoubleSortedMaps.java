package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Byte2DoubleSortedMaps {
   public static final Byte2DoubleSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2DoubleSortedMaps.EmptySortedMap();

   private Byte2DoubleSortedMaps() {
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().byteValue(), y.getKey().byteValue());
   }

   public static ObjectBidirectionalIterator<Byte2DoubleMap.Entry> fastIterator(Byte2DoubleSortedMap map) {
      ObjectSortedSet<Byte2DoubleMap.Entry> entries = map.byte2DoubleEntrySet();
      return entries instanceof Byte2DoubleSortedMap.FastSortedEntrySet
         ? ((Byte2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2DoubleMap.Entry> fastIterable(Byte2DoubleSortedMap map) {
      ObjectSortedSet<Byte2DoubleMap.Entry> entries = map.byte2DoubleEntrySet();
      return (ObjectBidirectionalIterable<Byte2DoubleMap.Entry>)(entries instanceof Byte2DoubleSortedMap.FastSortedEntrySet
         ? ((Byte2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Byte2DoubleSortedMap singleton(Byte key, Double value) {
      return new Byte2DoubleSortedMaps.Singleton(key, value);
   }

   public static Byte2DoubleSortedMap singleton(Byte key, Double value, ByteComparator comparator) {
      return new Byte2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2DoubleSortedMap singleton(byte key, double value) {
      return new Byte2DoubleSortedMaps.Singleton(key, value);
   }

   public static Byte2DoubleSortedMap singleton(byte key, double value, ByteComparator comparator) {
      return new Byte2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2DoubleSortedMap synchronize(Byte2DoubleSortedMap m) {
      return new Byte2DoubleSortedMaps.SynchronizedSortedMap(m);
   }

   public static Byte2DoubleSortedMap synchronize(Byte2DoubleSortedMap m, Object sync) {
      return new Byte2DoubleSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Byte2DoubleSortedMap unmodifiable(Byte2DoubleSortedMap m) {
      return new Byte2DoubleSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Byte2DoubleMaps.EmptyMap implements Byte2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Double>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      @Override
      public Byte2DoubleSortedMap subMap(byte from, byte to) {
         return Byte2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2DoubleSortedMap headMap(byte to) {
         return Byte2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2DoubleSortedMap tailMap(byte from) {
         return Byte2DoubleSortedMaps.EMPTY_MAP;
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
      public Byte2DoubleSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2DoubleSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2DoubleSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class Singleton extends Byte2DoubleMaps.Singleton implements Byte2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte key, double value, ByteComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(byte key, double value) {
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
      public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractByte2DoubleMap.BasicEntry(this.key, this.value), Byte2DoubleSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Byte2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Double>> entrySet() {
         return this.byte2DoubleEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2DoubleSortedMap subMap(byte from, byte to) {
         return (Byte2DoubleSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Byte2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2DoubleSortedMap headMap(byte to) {
         return (Byte2DoubleSortedMap)(this.compare(this.key, to) < 0 ? this : Byte2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2DoubleSortedMap tailMap(byte from) {
         return (Byte2DoubleSortedMap)(this.compare(from, this.key) <= 0 ? this : Byte2DoubleSortedMaps.EMPTY_MAP);
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
      public Byte2DoubleSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2DoubleSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2DoubleSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class SynchronizedSortedMap extends Byte2DoubleMaps.SynchronizedMap implements Byte2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2DoubleSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2DoubleSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Byte2DoubleSortedMap m) {
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
      public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2DoubleEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Byte2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Double>> entrySet() {
         return this.byte2DoubleEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2DoubleSortedMap subMap(byte from, byte to) {
         return new Byte2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Byte2DoubleSortedMap headMap(byte to) {
         return new Byte2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Byte2DoubleSortedMap tailMap(byte from) {
         return new Byte2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Byte2DoubleSortedMap subMap(Byte from, Byte to) {
         return new Byte2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2DoubleSortedMap headMap(Byte to) {
         return new Byte2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2DoubleSortedMap tailMap(Byte from) {
         return new Byte2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Byte2DoubleMaps.UnmodifiableMap implements Byte2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2DoubleSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2DoubleEntrySet());
         }

         return (ObjectSortedSet<Byte2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Double>> entrySet() {
         return this.byte2DoubleEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2DoubleSortedMap subMap(byte from, byte to) {
         return new Byte2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Byte2DoubleSortedMap headMap(byte to) {
         return new Byte2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Byte2DoubleSortedMap tailMap(byte from) {
         return new Byte2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Byte2DoubleSortedMap subMap(Byte from, Byte to) {
         return new Byte2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Byte2DoubleSortedMap headMap(Byte to) {
         return new Byte2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Byte2DoubleSortedMap tailMap(Byte from) {
         return new Byte2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
