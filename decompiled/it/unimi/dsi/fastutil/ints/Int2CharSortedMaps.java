package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Int2CharSortedMaps {
   public static final Int2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Int2CharSortedMaps.EmptySortedMap();

   private Int2CharSortedMaps() {
   }

   public static Comparator<? super Entry<Integer, ?>> entryComparator(IntComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().intValue(), y.getKey().intValue());
   }

   public static ObjectBidirectionalIterator<Int2CharMap.Entry> fastIterator(Int2CharSortedMap map) {
      ObjectSortedSet<Int2CharMap.Entry> entries = map.int2CharEntrySet();
      return entries instanceof Int2CharSortedMap.FastSortedEntrySet ? ((Int2CharSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Int2CharMap.Entry> fastIterable(Int2CharSortedMap map) {
      ObjectSortedSet<Int2CharMap.Entry> entries = map.int2CharEntrySet();
      return (ObjectBidirectionalIterable<Int2CharMap.Entry>)(entries instanceof Int2CharSortedMap.FastSortedEntrySet
         ? ((Int2CharSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Int2CharSortedMap singleton(Integer key, Character value) {
      return new Int2CharSortedMaps.Singleton(key, value);
   }

   public static Int2CharSortedMap singleton(Integer key, Character value, IntComparator comparator) {
      return new Int2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2CharSortedMap singleton(int key, char value) {
      return new Int2CharSortedMaps.Singleton(key, value);
   }

   public static Int2CharSortedMap singleton(int key, char value, IntComparator comparator) {
      return new Int2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Int2CharSortedMap synchronize(Int2CharSortedMap m) {
      return new Int2CharSortedMaps.SynchronizedSortedMap(m);
   }

   public static Int2CharSortedMap synchronize(Int2CharSortedMap m, Object sync) {
      return new Int2CharSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Int2CharSortedMap unmodifiable(Int2CharSortedMap m) {
      return new Int2CharSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Int2CharMaps.EmptyMap implements Int2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public IntComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Int2CharMap.Entry> int2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public IntSortedSet keySet() {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public Int2CharSortedMap subMap(int from, int to) {
         return Int2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2CharSortedMap headMap(int to) {
         return Int2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Int2CharSortedMap tailMap(int from) {
         return Int2CharSortedMaps.EMPTY_MAP;
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
      public Int2CharSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2CharSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2CharSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class Singleton extends Int2CharMaps.Singleton implements Int2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntComparator comparator;

      protected Singleton(int key, char value, IntComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(int key, char value) {
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
      public ObjectSortedSet<Int2CharMap.Entry> int2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractInt2CharMap.BasicEntry(this.key, this.value), Int2CharSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Int2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Character>> entrySet() {
         return this.int2CharEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.singleton(this.key, this.comparator);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2CharSortedMap subMap(int from, int to) {
         return (Int2CharSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Int2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2CharSortedMap headMap(int to) {
         return (Int2CharSortedMap)(this.compare(this.key, to) < 0 ? this : Int2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Int2CharSortedMap tailMap(int from) {
         return (Int2CharSortedMap)(this.compare(from, this.key) <= 0 ? this : Int2CharSortedMaps.EMPTY_MAP);
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
      public Int2CharSortedMap headMap(Integer oto) {
         return this.headMap(oto.intValue());
      }

      @Deprecated
      @Override
      public Int2CharSortedMap tailMap(Integer ofrom) {
         return this.tailMap(ofrom.intValue());
      }

      @Deprecated
      @Override
      public Int2CharSortedMap subMap(Integer ofrom, Integer oto) {
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

   public static class SynchronizedSortedMap extends Int2CharMaps.SynchronizedMap implements Int2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Int2CharSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Int2CharSortedMap m) {
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
      public ObjectSortedSet<Int2CharMap.Entry> int2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Int2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Character>> entrySet() {
         return this.int2CharEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2CharSortedMap subMap(int from, int to) {
         return new Int2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Int2CharSortedMap headMap(int to) {
         return new Int2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Int2CharSortedMap tailMap(int from) {
         return new Int2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Int2CharSortedMap subMap(Integer from, Integer to) {
         return new Int2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Int2CharSortedMap headMap(Integer to) {
         return new Int2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Int2CharSortedMap tailMap(Integer from) {
         return new Int2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Int2CharMaps.UnmodifiableMap implements Int2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Int2CharSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public IntComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Int2CharMap.Entry> int2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2CharEntrySet());
         }

         return (ObjectSortedSet<Int2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Integer, Character>> entrySet() {
         return this.int2CharEntrySet();
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (IntSortedSet)this.keys;
      }

      @Override
      public Int2CharSortedMap subMap(int from, int to) {
         return new Int2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Int2CharSortedMap headMap(int to) {
         return new Int2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Int2CharSortedMap tailMap(int from) {
         return new Int2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Int2CharSortedMap subMap(Integer from, Integer to) {
         return new Int2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Int2CharSortedMap headMap(Integer to) {
         return new Int2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Int2CharSortedMap tailMap(Integer from) {
         return new Int2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
