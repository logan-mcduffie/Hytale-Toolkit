package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Int2FloatSortedMaps {
   public static final Int2FloatSortedMaps.EmptySortedMap EMPTY_MAP = new Int2FloatSortedMaps.EmptySortedMap();

   private Int2FloatSortedMaps() {
   }

   public static Comparator<? super Entry<Integer, ?>> entryComparator(IntComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().intValue(), y.getKey().intValue());
   }

   public static ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator(Int2FloatSortedMap map) {
      ObjectSortedSet<Int2FloatMap.Entry> entries = map.int2FloatEntrySet();
      return entries instanceof Int2FloatSortedMap.FastSortedEntrySet ? ((Int2FloatSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Int2FloatMap.Entry> fastIterable(Int2FloatSortedMap map) {
      ObjectSortedSet<Int2FloatMap.Entry> entries = map.int2FloatEntrySet();
      return (ObjectBidirectionalIterable<Int2FloatMap.Entry>)(entries instanceof Int2FloatSortedMap.FastSortedEntrySet
         ? ((Int2FloatSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Int2FloatSortedMap singleton(Integer key, Float value) {
      return new Int2FloatSortedMaps.Singleton(key, value);
   }

   public static Int2FloatSortedMap singleton(Integer key, Float value, IntComparator comparator) {
      return new Int2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2FloatSortedMap singleton(int key, float value) {
      return new Int2FloatSortedMaps.Singleton(key, value);
   }

   public static Int2FloatSortedMap singleton(int key, float value, IntComparator comparator) {
      return new Int2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2FloatSortedMap synchronize(Int2FloatSortedMap m) {
      return new Int2FloatSortedMaps.SynchronizedSortedMap(m);
   }

   public static Int2FloatSortedMap synchronize(Int2FloatSortedMap m, Object sync) {
      return new Int2FloatSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Int2FloatSortedMap unmodifiable(Int2FloatSortedMap m) {
      return new Int2FloatSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Int2FloatMaps.EmptyMap implements Int2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public IntComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Float>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public IntSortedSet keySet() {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public Int2FloatSortedMap subMap(int from, int to) {
         return Int2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2FloatSortedMap headMap(int to) {
         return Int2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2FloatSortedMap tailMap(int from) {
         return Int2FloatSortedMaps.EMPTY_MAP;
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
      public Int2FloatSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2FloatSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2FloatSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class Singleton extends Int2FloatMaps.Singleton implements Int2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntComparator comparator;

      protected Singleton(int key, float value, IntComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(int key, float value) {
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
      public ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractInt2FloatMap.BasicEntry(this.key, this.value), Int2FloatSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Int2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.singleton(this.key, this.comparator);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2FloatSortedMap subMap(int from, int to) {
         return (Int2FloatSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Int2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2FloatSortedMap headMap(int to) {
         return (Int2FloatSortedMap)(this.compare(this.key, to) < 0 ? this : Int2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2FloatSortedMap tailMap(int from) {
         return (Int2FloatSortedMap)(this.compare(from, this.key) <= 0 ? this : Int2FloatSortedMaps.EMPTY_MAP);
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
      public Int2FloatSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2FloatSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2FloatSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class SynchronizedSortedMap extends Int2FloatMaps.SynchronizedMap implements Int2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2FloatSortedMap sortedMap;

      protected SynchronizedSortedMap(Int2FloatSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Int2FloatSortedMap m) {
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
      public ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2FloatEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Int2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2FloatSortedMap subMap(int from, int to) {
         return new Int2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Int2FloatSortedMap headMap(int to) {
         return new Int2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Int2FloatSortedMap tailMap(int from) {
         return new Int2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Int2FloatSortedMap subMap(Integer from, Integer to) {
         return new Int2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Int2FloatSortedMap headMap(Integer to) {
         return new Int2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Int2FloatSortedMap tailMap(Integer from) {
         return new Int2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Int2FloatMaps.UnmodifiableMap implements Int2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2FloatSortedMap sortedMap;

      protected UnmodifiableSortedMap(Int2FloatSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public IntComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2FloatEntrySet());
         }

         return (ObjectSortedSet<Int2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2FloatSortedMap subMap(int from, int to) {
         return new Int2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Int2FloatSortedMap headMap(int to) {
         return new Int2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Int2FloatSortedMap tailMap(int from) {
         return new Int2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Int2FloatSortedMap subMap(Integer from, Integer to) {
         return new Int2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Int2FloatSortedMap headMap(Integer to) {
         return new Int2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Int2FloatSortedMap tailMap(Integer from) {
         return new Int2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
