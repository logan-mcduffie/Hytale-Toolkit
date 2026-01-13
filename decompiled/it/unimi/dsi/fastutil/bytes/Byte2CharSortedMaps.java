package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Byte2CharSortedMaps {
   public static final Byte2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2CharSortedMaps.EmptySortedMap();

   private Byte2CharSortedMaps() {
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().byteValue(), y.getKey().byteValue());
   }

   public static ObjectBidirectionalIterator<Byte2CharMap.Entry> fastIterator(Byte2CharSortedMap map) {
      ObjectSortedSet<Byte2CharMap.Entry> entries = map.byte2CharEntrySet();
      return entries instanceof Byte2CharSortedMap.FastSortedEntrySet ? ((Byte2CharSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2CharMap.Entry> fastIterable(Byte2CharSortedMap map) {
      ObjectSortedSet<Byte2CharMap.Entry> entries = map.byte2CharEntrySet();
      return (ObjectBidirectionalIterable<Byte2CharMap.Entry>)(entries instanceof Byte2CharSortedMap.FastSortedEntrySet
         ? ((Byte2CharSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Byte2CharSortedMap singleton(Byte key, Character value) {
      return new Byte2CharSortedMaps.Singleton(key, value);
   }

   public static Byte2CharSortedMap singleton(Byte key, Character value, ByteComparator comparator) {
      return new Byte2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2CharSortedMap singleton(byte key, char value) {
      return new Byte2CharSortedMaps.Singleton(key, value);
   }

   public static Byte2CharSortedMap singleton(byte key, char value, ByteComparator comparator) {
      return new Byte2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2CharSortedMap synchronize(Byte2CharSortedMap m) {
      return new Byte2CharSortedMaps.SynchronizedSortedMap(m);
   }

   public static Byte2CharSortedMap synchronize(Byte2CharSortedMap m, Object sync) {
      return new Byte2CharSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Byte2CharSortedMap unmodifiable(Byte2CharSortedMap m) {
      return new Byte2CharSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Byte2CharMaps.EmptyMap implements Byte2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      @Override
      public Byte2CharSortedMap subMap(byte from, byte to) {
         return Byte2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2CharSortedMap headMap(byte to) {
         return Byte2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2CharSortedMap tailMap(byte from) {
         return Byte2CharSortedMaps.EMPTY_MAP;
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
      public Byte2CharSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2CharSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2CharSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class Singleton extends Byte2CharMaps.Singleton implements Byte2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte key, char value, ByteComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(byte key, char value) {
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
      public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractByte2CharMap.BasicEntry(this.key, this.value), Byte2CharSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Byte2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Character>> entrySet() {
         return this.byte2CharEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2CharSortedMap subMap(byte from, byte to) {
         return (Byte2CharSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Byte2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2CharSortedMap headMap(byte to) {
         return (Byte2CharSortedMap)(this.compare(this.key, to) < 0 ? this : Byte2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2CharSortedMap tailMap(byte from) {
         return (Byte2CharSortedMap)(this.compare(from, this.key) <= 0 ? this : Byte2CharSortedMaps.EMPTY_MAP);
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
      public Byte2CharSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2CharSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2CharSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class SynchronizedSortedMap extends Byte2CharMaps.SynchronizedMap implements Byte2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2CharSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Byte2CharSortedMap m) {
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
      public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Byte2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Character>> entrySet() {
         return this.byte2CharEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2CharSortedMap subMap(byte from, byte to) {
         return new Byte2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Byte2CharSortedMap headMap(byte to) {
         return new Byte2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Byte2CharSortedMap tailMap(byte from) {
         return new Byte2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Byte2CharSortedMap subMap(Byte from, Byte to) {
         return new Byte2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2CharSortedMap headMap(Byte to) {
         return new Byte2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2CharSortedMap tailMap(Byte from) {
         return new Byte2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Byte2CharMaps.UnmodifiableMap implements Byte2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2CharSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2CharEntrySet());
         }

         return (ObjectSortedSet<Byte2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Character>> entrySet() {
         return this.byte2CharEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2CharSortedMap subMap(byte from, byte to) {
         return new Byte2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Byte2CharSortedMap headMap(byte to) {
         return new Byte2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Byte2CharSortedMap tailMap(byte from) {
         return new Byte2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Byte2CharSortedMap subMap(Byte from, Byte to) {
         return new Byte2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Byte2CharSortedMap headMap(Byte to) {
         return new Byte2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Byte2CharSortedMap tailMap(Byte from) {
         return new Byte2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
