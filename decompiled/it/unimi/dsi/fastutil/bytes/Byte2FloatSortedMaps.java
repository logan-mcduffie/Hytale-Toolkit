package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Byte2FloatSortedMaps {
   public static final Byte2FloatSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2FloatSortedMaps.EmptySortedMap();

   private Byte2FloatSortedMaps() {
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().byteValue(), y.getKey().byteValue());
   }

   public static ObjectBidirectionalIterator<Byte2FloatMap.Entry> fastIterator(Byte2FloatSortedMap map) {
      ObjectSortedSet<Byte2FloatMap.Entry> entries = map.byte2FloatEntrySet();
      return entries instanceof Byte2FloatSortedMap.FastSortedEntrySet ? ((Byte2FloatSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2FloatMap.Entry> fastIterable(Byte2FloatSortedMap map) {
      ObjectSortedSet<Byte2FloatMap.Entry> entries = map.byte2FloatEntrySet();
      return (ObjectBidirectionalIterable<Byte2FloatMap.Entry>)(entries instanceof Byte2FloatSortedMap.FastSortedEntrySet
         ? ((Byte2FloatSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Byte2FloatSortedMap singleton(Byte key, Float value) {
      return new Byte2FloatSortedMaps.Singleton(key, value);
   }

   public static Byte2FloatSortedMap singleton(Byte key, Float value, ByteComparator comparator) {
      return new Byte2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2FloatSortedMap singleton(byte key, float value) {
      return new Byte2FloatSortedMaps.Singleton(key, value);
   }

   public static Byte2FloatSortedMap singleton(byte key, float value, ByteComparator comparator) {
      return new Byte2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2FloatSortedMap synchronize(Byte2FloatSortedMap m) {
      return new Byte2FloatSortedMaps.SynchronizedSortedMap(m);
   }

   public static Byte2FloatSortedMap synchronize(Byte2FloatSortedMap m, Object sync) {
      return new Byte2FloatSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Byte2FloatSortedMap unmodifiable(Byte2FloatSortedMap m) {
      return new Byte2FloatSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Byte2FloatMaps.EmptyMap implements Byte2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Float>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      @Override
      public Byte2FloatSortedMap subMap(byte from, byte to) {
         return Byte2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2FloatSortedMap headMap(byte to) {
         return Byte2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2FloatSortedMap tailMap(byte from) {
         return Byte2FloatSortedMaps.EMPTY_MAP;
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
      public Byte2FloatSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2FloatSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2FloatSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class Singleton extends Byte2FloatMaps.Singleton implements Byte2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte key, float value, ByteComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(byte key, float value) {
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
      public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractByte2FloatMap.BasicEntry(this.key, this.value), Byte2FloatSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Byte2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2FloatSortedMap subMap(byte from, byte to) {
         return (Byte2FloatSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Byte2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2FloatSortedMap headMap(byte to) {
         return (Byte2FloatSortedMap)(this.compare(this.key, to) < 0 ? this : Byte2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2FloatSortedMap tailMap(byte from) {
         return (Byte2FloatSortedMap)(this.compare(from, this.key) <= 0 ? this : Byte2FloatSortedMaps.EMPTY_MAP);
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
      public Byte2FloatSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2FloatSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2FloatSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class SynchronizedSortedMap extends Byte2FloatMaps.SynchronizedMap implements Byte2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2FloatSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2FloatSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Byte2FloatSortedMap m) {
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
      public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2FloatEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Byte2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2FloatSortedMap subMap(byte from, byte to) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Byte2FloatSortedMap headMap(byte to) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Byte2FloatSortedMap tailMap(byte from) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Byte2FloatSortedMap subMap(Byte from, Byte to) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2FloatSortedMap headMap(Byte to) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2FloatSortedMap tailMap(Byte from) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Byte2FloatMaps.UnmodifiableMap implements Byte2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2FloatSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2FloatSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2FloatEntrySet());
         }

         return (ObjectSortedSet<Byte2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2FloatSortedMap subMap(byte from, byte to) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Byte2FloatSortedMap headMap(byte to) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Byte2FloatSortedMap tailMap(byte from) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Byte2FloatSortedMap subMap(Byte from, Byte to) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Byte2FloatSortedMap headMap(Byte to) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Byte2FloatSortedMap tailMap(Byte from) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
