package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Byte2BooleanSortedMaps {
   public static final Byte2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2BooleanSortedMaps.EmptySortedMap();

   private Byte2BooleanSortedMaps() {
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().byteValue(), y.getKey().byteValue());
   }

   public static ObjectBidirectionalIterator<Byte2BooleanMap.Entry> fastIterator(Byte2BooleanSortedMap map) {
      ObjectSortedSet<Byte2BooleanMap.Entry> entries = map.byte2BooleanEntrySet();
      return entries instanceof Byte2BooleanSortedMap.FastSortedEntrySet
         ? ((Byte2BooleanSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2BooleanMap.Entry> fastIterable(Byte2BooleanSortedMap map) {
      ObjectSortedSet<Byte2BooleanMap.Entry> entries = map.byte2BooleanEntrySet();
      return (ObjectBidirectionalIterable<Byte2BooleanMap.Entry>)(entries instanceof Byte2BooleanSortedMap.FastSortedEntrySet
         ? ((Byte2BooleanSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Byte2BooleanSortedMap singleton(Byte key, Boolean value) {
      return new Byte2BooleanSortedMaps.Singleton(key, value);
   }

   public static Byte2BooleanSortedMap singleton(Byte key, Boolean value, ByteComparator comparator) {
      return new Byte2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2BooleanSortedMap singleton(byte key, boolean value) {
      return new Byte2BooleanSortedMaps.Singleton(key, value);
   }

   public static Byte2BooleanSortedMap singleton(byte key, boolean value, ByteComparator comparator) {
      return new Byte2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Byte2BooleanSortedMap synchronize(Byte2BooleanSortedMap m) {
      return new Byte2BooleanSortedMaps.SynchronizedSortedMap(m);
   }

   public static Byte2BooleanSortedMap synchronize(Byte2BooleanSortedMap m, Object sync) {
      return new Byte2BooleanSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Byte2BooleanSortedMap unmodifiable(Byte2BooleanSortedMap m) {
      return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Byte2BooleanMaps.EmptyMap implements Byte2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      @Override
      public Byte2BooleanSortedMap subMap(byte from, byte to) {
         return Byte2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2BooleanSortedMap headMap(byte to) {
         return Byte2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Byte2BooleanSortedMap tailMap(byte from) {
         return Byte2BooleanSortedMaps.EMPTY_MAP;
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
      public Byte2BooleanSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2BooleanSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2BooleanSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class Singleton extends Byte2BooleanMaps.Singleton implements Byte2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte key, boolean value, ByteComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(byte key, boolean value) {
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
      public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractByte2BooleanMap.BasicEntry(this.key, this.value), Byte2BooleanSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Byte2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2BooleanSortedMap subMap(byte from, byte to) {
         return (Byte2BooleanSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Byte2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2BooleanSortedMap headMap(byte to) {
         return (Byte2BooleanSortedMap)(this.compare(this.key, to) < 0 ? this : Byte2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Byte2BooleanSortedMap tailMap(byte from) {
         return (Byte2BooleanSortedMap)(this.compare(from, this.key) <= 0 ? this : Byte2BooleanSortedMaps.EMPTY_MAP);
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
      public Byte2BooleanSortedMap headMap(Byte oto) {
         return this.headMap(oto.byteValue());
      }

      @Deprecated
      @Override
      public Byte2BooleanSortedMap tailMap(Byte ofrom) {
         return this.tailMap(ofrom.byteValue());
      }

      @Deprecated
      @Override
      public Byte2BooleanSortedMap subMap(Byte ofrom, Byte oto) {
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

   public static class SynchronizedSortedMap extends Byte2BooleanMaps.SynchronizedMap implements Byte2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2BooleanSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Byte2BooleanSortedMap m) {
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
      public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Byte2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2BooleanSortedMap subMap(byte from, byte to) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Byte2BooleanSortedMap headMap(byte to) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Byte2BooleanSortedMap tailMap(byte from) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Byte2BooleanSortedMap subMap(Byte from, Byte to) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2BooleanSortedMap headMap(Byte to) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Byte2BooleanSortedMap tailMap(Byte from) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Byte2BooleanMaps.UnmodifiableMap implements Byte2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2BooleanSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2BooleanEntrySet());
         }

         return (ObjectSortedSet<Byte2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      @Override
      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      @Override
      public Byte2BooleanSortedMap subMap(byte from, byte to) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Byte2BooleanSortedMap headMap(byte to) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Byte2BooleanSortedMap tailMap(byte from) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Byte2BooleanSortedMap subMap(Byte from, Byte to) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Byte2BooleanSortedMap headMap(Byte to) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Byte2BooleanSortedMap tailMap(Byte from) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
