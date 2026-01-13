package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Int2LongSortedMaps {
   public static final Int2LongSortedMaps.EmptySortedMap EMPTY_MAP = new Int2LongSortedMaps.EmptySortedMap();

   private Int2LongSortedMaps() {
   }

   public static Comparator<? super Entry<Integer, ?>> entryComparator(IntComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().intValue(), y.getKey().intValue());
   }

   public static ObjectBidirectionalIterator<Int2LongMap.Entry> fastIterator(Int2LongSortedMap map) {
      ObjectSortedSet<Int2LongMap.Entry> entries = map.int2LongEntrySet();
      return entries instanceof Int2LongSortedMap.FastSortedEntrySet ? ((Int2LongSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Int2LongMap.Entry> fastIterable(Int2LongSortedMap map) {
      ObjectSortedSet<Int2LongMap.Entry> entries = map.int2LongEntrySet();
      return (ObjectBidirectionalIterable<Int2LongMap.Entry>)(entries instanceof Int2LongSortedMap.FastSortedEntrySet
         ? ((Int2LongSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Int2LongSortedMap singleton(Integer key, Long value) {
      return new Int2LongSortedMaps.Singleton(key, value);
   }

   public static Int2LongSortedMap singleton(Integer key, Long value, IntComparator comparator) {
      return new Int2LongSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2LongSortedMap singleton(int key, long value) {
      return new Int2LongSortedMaps.Singleton(key, value);
   }

   public static Int2LongSortedMap singleton(int key, long value, IntComparator comparator) {
      return new Int2LongSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2LongSortedMap synchronize(Int2LongSortedMap m) {
      return new Int2LongSortedMaps.SynchronizedSortedMap(m);
   }

   public static Int2LongSortedMap synchronize(Int2LongSortedMap m, Object sync) {
      return new Int2LongSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Int2LongSortedMap unmodifiable(Int2LongSortedMap m) {
      return new Int2LongSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Int2LongMaps.EmptyMap implements Int2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public IntComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Long>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public IntSortedSet keySet() {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public Int2LongSortedMap subMap(int from, int to) {
         return Int2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2LongSortedMap headMap(int to) {
         return Int2LongSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2LongSortedMap tailMap(int from) {
         return Int2LongSortedMaps.EMPTY_MAP;
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
      public Int2LongSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2LongSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2LongSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class Singleton extends Int2LongMaps.Singleton implements Int2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntComparator comparator;

      protected Singleton(int key, long value, IntComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(int key, long value) {
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
      public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractInt2LongMap.BasicEntry(this.key, this.value), Int2LongSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Int2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.singleton(this.key, this.comparator);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2LongSortedMap subMap(int from, int to) {
         return (Int2LongSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Int2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2LongSortedMap headMap(int to) {
         return (Int2LongSortedMap)(this.compare(this.key, to) < 0 ? this : Int2LongSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2LongSortedMap tailMap(int from) {
         return (Int2LongSortedMap)(this.compare(from, this.key) <= 0 ? this : Int2LongSortedMaps.EMPTY_MAP);
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
      public Int2LongSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2LongSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2LongSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class SynchronizedSortedMap extends Int2LongMaps.SynchronizedMap implements Int2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2LongSortedMap sortedMap;

      protected SynchronizedSortedMap(Int2LongSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Int2LongSortedMap m) {
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
      public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2LongEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Int2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2LongSortedMap subMap(int from, int to) {
         return new Int2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Int2LongSortedMap headMap(int to) {
         return new Int2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Int2LongSortedMap tailMap(int from) {
         return new Int2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Int2LongSortedMap subMap(Integer from, Integer to) {
         return new Int2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Int2LongSortedMap headMap(Integer to) {
         return new Int2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Int2LongSortedMap tailMap(Integer from) {
         return new Int2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Int2LongMaps.UnmodifiableMap implements Int2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2LongSortedMap sortedMap;

      protected UnmodifiableSortedMap(Int2LongSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public IntComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2LongEntrySet());
         }

         return (ObjectSortedSet<Int2LongMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2LongSortedMap subMap(int from, int to) {
         return new Int2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Int2LongSortedMap headMap(int to) {
         return new Int2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Int2LongSortedMap tailMap(int from) {
         return new Int2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Int2LongSortedMap subMap(Integer from, Integer to) {
         return new Int2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Int2LongSortedMap headMap(Integer to) {
         return new Int2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Int2LongSortedMap tailMap(Integer from) {
         return new Int2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
