package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Int2BooleanSortedMaps {
   public static final Int2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Int2BooleanSortedMaps.EmptySortedMap();

   private Int2BooleanSortedMaps() {
   }

   public static Comparator<? super Entry<Integer, ?>> entryComparator(IntComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().intValue(), y.getKey().intValue());
   }

   public static ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator(Int2BooleanSortedMap map) {
      ObjectSortedSet<Int2BooleanMap.Entry> entries = map.int2BooleanEntrySet();
      return entries instanceof Int2BooleanSortedMap.FastSortedEntrySet
         ? ((Int2BooleanSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Int2BooleanMap.Entry> fastIterable(Int2BooleanSortedMap map) {
      ObjectSortedSet<Int2BooleanMap.Entry> entries = map.int2BooleanEntrySet();
      return (ObjectBidirectionalIterable<Int2BooleanMap.Entry>)(entries instanceof Int2BooleanSortedMap.FastSortedEntrySet
         ? ((Int2BooleanSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Int2BooleanSortedMap singleton(Integer key, Boolean value) {
      return new Int2BooleanSortedMaps.Singleton(key, value);
   }

   public static Int2BooleanSortedMap singleton(Integer key, Boolean value, IntComparator comparator) {
      return new Int2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2BooleanSortedMap singleton(int key, boolean value) {
      return new Int2BooleanSortedMaps.Singleton(key, value);
   }

   public static Int2BooleanSortedMap singleton(int key, boolean value, IntComparator comparator) {
      return new Int2BooleanSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2BooleanSortedMap synchronize(Int2BooleanSortedMap m) {
      return new Int2BooleanSortedMaps.SynchronizedSortedMap(m);
   }

   public static Int2BooleanSortedMap synchronize(Int2BooleanSortedMap m, Object sync) {
      return new Int2BooleanSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Int2BooleanSortedMap unmodifiable(Int2BooleanSortedMap m) {
      return new Int2BooleanSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Int2BooleanMaps.EmptyMap implements Int2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public IntComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public IntSortedSet keySet() {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public Int2BooleanSortedMap subMap(int from, int to) {
         return Int2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2BooleanSortedMap headMap(int to) {
         return Int2BooleanSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2BooleanSortedMap tailMap(int from) {
         return Int2BooleanSortedMaps.EMPTY_MAP;
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
      public Int2BooleanSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2BooleanSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2BooleanSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class Singleton extends Int2BooleanMaps.Singleton implements Int2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntComparator comparator;

      protected Singleton(int key, boolean value, IntComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(int key, boolean value) {
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
      public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractInt2BooleanMap.BasicEntry(this.key, this.value), Int2BooleanSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Int2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.singleton(this.key, this.comparator);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2BooleanSortedMap subMap(int from, int to) {
         return (Int2BooleanSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Int2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2BooleanSortedMap headMap(int to) {
         return (Int2BooleanSortedMap)(this.compare(this.key, to) < 0 ? this : Int2BooleanSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2BooleanSortedMap tailMap(int from) {
         return (Int2BooleanSortedMap)(this.compare(from, this.key) <= 0 ? this : Int2BooleanSortedMaps.EMPTY_MAP);
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
      public Int2BooleanSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2BooleanSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2BooleanSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class SynchronizedSortedMap extends Int2BooleanMaps.SynchronizedMap implements Int2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Int2BooleanSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Int2BooleanSortedMap m) {
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
      public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Int2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2BooleanSortedMap subMap(int from, int to) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Int2BooleanSortedMap headMap(int to) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Int2BooleanSortedMap tailMap(int from) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Int2BooleanSortedMap subMap(Integer from, Integer to) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Int2BooleanSortedMap headMap(Integer to) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Int2BooleanSortedMap tailMap(Integer from) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Int2BooleanMaps.UnmodifiableMap implements Int2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Int2BooleanSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public IntComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2BooleanEntrySet());
         }

         return (ObjectSortedSet<Int2BooleanMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2BooleanSortedMap subMap(int from, int to) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Int2BooleanSortedMap headMap(int to) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Int2BooleanSortedMap tailMap(int from) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Int2BooleanSortedMap subMap(Integer from, Integer to) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Int2BooleanSortedMap headMap(Integer to) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Int2BooleanSortedMap tailMap(Integer from) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
