package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Int2ByteSortedMaps {
   public static final Int2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Int2ByteSortedMaps.EmptySortedMap();

   private Int2ByteSortedMaps() {
   }

   public static Comparator<? super Entry<Integer, ?>> entryComparator(IntComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().intValue(), y.getKey().intValue());
   }

   public static ObjectBidirectionalIterator<Int2ByteMap.Entry> fastIterator(Int2ByteSortedMap map) {
      ObjectSortedSet<Int2ByteMap.Entry> entries = map.int2ByteEntrySet();
      return entries instanceof Int2ByteSortedMap.FastSortedEntrySet ? ((Int2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Int2ByteMap.Entry> fastIterable(Int2ByteSortedMap map) {
      ObjectSortedSet<Int2ByteMap.Entry> entries = map.int2ByteEntrySet();
      return (ObjectBidirectionalIterable<Int2ByteMap.Entry>)(entries instanceof Int2ByteSortedMap.FastSortedEntrySet
         ? ((Int2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Int2ByteSortedMap singleton(Integer key, Byte value) {
      return new Int2ByteSortedMaps.Singleton(key, value);
   }

   public static Int2ByteSortedMap singleton(Integer key, Byte value, IntComparator comparator) {
      return new Int2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2ByteSortedMap singleton(int key, byte value) {
      return new Int2ByteSortedMaps.Singleton(key, value);
   }

   public static Int2ByteSortedMap singleton(int key, byte value, IntComparator comparator) {
      return new Int2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2ByteSortedMap synchronize(Int2ByteSortedMap m) {
      return new Int2ByteSortedMaps.SynchronizedSortedMap(m);
   }

   public static Int2ByteSortedMap synchronize(Int2ByteSortedMap m, Object sync) {
      return new Int2ByteSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Int2ByteSortedMap unmodifiable(Int2ByteSortedMap m) {
      return new Int2ByteSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Int2ByteMaps.EmptyMap implements Int2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public IntComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public IntSortedSet keySet() {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public Int2ByteSortedMap subMap(int from, int to) {
         return Int2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2ByteSortedMap headMap(int to) {
         return Int2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2ByteSortedMap tailMap(int from) {
         return Int2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public int firstIntKey() {
         throw new NoSuchElementException();
      }

      @Override
      public int lastIntKey() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap subMap(Integer ofrom, Integer oto) {
         return this.subMap(ofrom.intValue(), oto.intValue());
      }

      @Deprecated
      @Override
      public Integer firstKey() {
         return this.firstIntKey();
      }

      @Deprecated
      @Override
      public Integer lastKey() {
         return this.lastIntKey();
      }
   }

   public static class Singleton extends Int2ByteMaps.Singleton implements Int2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntComparator comparator;

      protected Singleton(int key, byte value, IntComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(int key, byte value) {
         this(key, value, null);
      }

      final int compare(int k1, int k2) {
         return this.comparator == null ? Integer.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public IntComparator comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractInt2ByteMap.BasicEntry(this.key, this.value), Int2ByteSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Int2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Byte>> entrySet() {
         return this.int2ByteEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.singleton(this.key, this.comparator);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2ByteSortedMap subMap(int from, int to) {
         return (Int2ByteSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Int2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2ByteSortedMap headMap(int to) {
         return (Int2ByteSortedMap)(this.compare(this.key, to) < 0 ? this : Int2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2ByteSortedMap tailMap(int from) {
         return (Int2ByteSortedMap)(this.compare(from, this.key) <= 0 ? this : Int2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public int firstIntKey() {
         return this.key;
      }

      @Override
      public int lastIntKey() {
         return this.key;
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap subMap(Integer ofrom, Integer oto) {
         return this.subMap(ofrom.intValue(), oto.intValue());
      }

      @Deprecated
      @Override
      public Integer firstKey() {
         return this.firstIntKey();
      }

      @Deprecated
      @Override
      public Integer lastKey() {
         return this.lastIntKey();
      }
   }

   public static class SynchronizedSortedMap extends Int2ByteMaps.SynchronizedMap implements Int2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ByteSortedMap sortedMap;

      protected SynchronizedSortedMap(Int2ByteSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Int2ByteSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public IntComparator comparator() {
         synchronized (this.sync) {
            return this.sortedMap.comparator();
         }
      }

      @Override
      public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Int2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Byte>> entrySet() {
         return this.int2ByteEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2ByteSortedMap subMap(int from, int to) {
         return new Int2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Int2ByteSortedMap headMap(int to) {
         return new Int2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Int2ByteSortedMap tailMap(int from) {
         return new Int2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }

      @Override
      public int firstIntKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstIntKey();
         }
      }

      @Override
      public int lastIntKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastIntKey();
         }
      }

      @Deprecated
      @Override
      public Integer firstKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      @Deprecated
      @Override
      public Integer lastKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap subMap(Integer from, Integer to) {
         return new Int2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap headMap(Integer to) {
         return new Int2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap tailMap(Integer from) {
         return new Int2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Int2ByteMaps.UnmodifiableMap implements Int2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ByteSortedMap sortedMap;

      protected UnmodifiableSortedMap(Int2ByteSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public IntComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2ByteEntrySet());
         }

         return (ObjectSortedSet<Int2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Byte>> entrySet() {
         return this.int2ByteEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2ByteSortedMap subMap(int from, int to) {
         return new Int2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Int2ByteSortedMap headMap(int to) {
         return new Int2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Int2ByteSortedMap tailMap(int from) {
         return new Int2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }

      @Override
      public int firstIntKey() {
         return this.sortedMap.firstIntKey();
      }

      @Override
      public int lastIntKey() {
         return this.sortedMap.lastIntKey();
      }

      @Deprecated
      @Override
      public Integer firstKey() {
         return this.sortedMap.firstKey();
      }

      @Deprecated
      @Override
      public Integer lastKey() {
         return this.sortedMap.lastKey();
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap subMap(Integer from, Integer to) {
         return new Int2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap headMap(Integer to) {
         return new Int2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Int2ByteSortedMap tailMap(Integer from) {
         return new Int2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
