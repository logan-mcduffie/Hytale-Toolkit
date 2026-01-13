package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Int2ShortSortedMaps {
   public static final Int2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Int2ShortSortedMaps.EmptySortedMap();

   private Int2ShortSortedMaps() {
   }

   public static Comparator<? super Entry<Integer, ?>> entryComparator(IntComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().intValue(), y.getKey().intValue());
   }

   public static ObjectBidirectionalIterator<Int2ShortMap.Entry> fastIterator(Int2ShortSortedMap map) {
      ObjectSortedSet<Int2ShortMap.Entry> entries = map.int2ShortEntrySet();
      return entries instanceof Int2ShortSortedMap.FastSortedEntrySet ? ((Int2ShortSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Int2ShortMap.Entry> fastIterable(Int2ShortSortedMap map) {
      ObjectSortedSet<Int2ShortMap.Entry> entries = map.int2ShortEntrySet();
      return (ObjectBidirectionalIterable<Int2ShortMap.Entry>)(entries instanceof Int2ShortSortedMap.FastSortedEntrySet
         ? ((Int2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Int2ShortSortedMap singleton(Integer key, Short value) {
      return new Int2ShortSortedMaps.Singleton(key, value);
   }

   public static Int2ShortSortedMap singleton(Integer key, Short value, IntComparator comparator) {
      return new Int2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2ShortSortedMap singleton(int key, short value) {
      return new Int2ShortSortedMaps.Singleton(key, value);
   }

   public static Int2ShortSortedMap singleton(int key, short value, IntComparator comparator) {
      return new Int2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2ShortSortedMap synchronize(Int2ShortSortedMap m) {
      return new Int2ShortSortedMaps.SynchronizedSortedMap(m);
   }

   public static Int2ShortSortedMap synchronize(Int2ShortSortedMap m, Object sync) {
      return new Int2ShortSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Int2ShortSortedMap unmodifiable(Int2ShortSortedMap m) {
      return new Int2ShortSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Int2ShortMaps.EmptyMap implements Int2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public IntComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Int2ShortMap.Entry> int2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public IntSortedSet keySet() {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public Int2ShortSortedMap subMap(int from, int to) {
         return Int2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2ShortSortedMap headMap(int to) {
         return Int2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2ShortSortedMap tailMap(int from) {
         return Int2ShortSortedMaps.EMPTY_MAP;
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
      public Int2ShortSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2ShortSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2ShortSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class Singleton extends Int2ShortMaps.Singleton implements Int2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntComparator comparator;

      protected Singleton(int key, short value, IntComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(int key, short value) {
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
      public ObjectSortedSet<Int2ShortMap.Entry> int2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractInt2ShortMap.BasicEntry(this.key, this.value), Int2ShortSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Int2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Short>> entrySet() {
         return this.int2ShortEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.singleton(this.key, this.comparator);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2ShortSortedMap subMap(int from, int to) {
         return (Int2ShortSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Int2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2ShortSortedMap headMap(int to) {
         return (Int2ShortSortedMap)(this.compare(this.key, to) < 0 ? this : Int2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2ShortSortedMap tailMap(int from) {
         return (Int2ShortSortedMap)(this.compare(from, this.key) <= 0 ? this : Int2ShortSortedMaps.EMPTY_MAP);
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
      public Int2ShortSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2ShortSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2ShortSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class SynchronizedSortedMap extends Int2ShortMaps.SynchronizedMap implements Int2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ShortSortedMap sortedMap;

      protected SynchronizedSortedMap(Int2ShortSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Int2ShortSortedMap m) {
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
      public ObjectSortedSet<Int2ShortMap.Entry> int2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Int2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Short>> entrySet() {
         return this.int2ShortEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2ShortSortedMap subMap(int from, int to) {
         return new Int2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Int2ShortSortedMap headMap(int to) {
         return new Int2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Int2ShortSortedMap tailMap(int from) {
         return new Int2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Int2ShortSortedMap subMap(Integer from, Integer to) {
         return new Int2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Int2ShortSortedMap headMap(Integer to) {
         return new Int2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Int2ShortSortedMap tailMap(Integer from) {
         return new Int2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Int2ShortMaps.UnmodifiableMap implements Int2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ShortSortedMap sortedMap;

      protected UnmodifiableSortedMap(Int2ShortSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public IntComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Int2ShortMap.Entry> int2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2ShortEntrySet());
         }

         return (ObjectSortedSet<Int2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Short>> entrySet() {
         return this.int2ShortEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2ShortSortedMap subMap(int from, int to) {
         return new Int2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Int2ShortSortedMap headMap(int to) {
         return new Int2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Int2ShortSortedMap tailMap(int from) {
         return new Int2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Int2ShortSortedMap subMap(Integer from, Integer to) {
         return new Int2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Int2ShortSortedMap headMap(Integer to) {
         return new Int2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Int2ShortSortedMap tailMap(Integer from) {
         return new Int2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
