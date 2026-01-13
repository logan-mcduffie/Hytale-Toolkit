package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Char2DoubleSortedMaps {
   public static final Char2DoubleSortedMaps.EmptySortedMap EMPTY_MAP = new Char2DoubleSortedMaps.EmptySortedMap();

   private Char2DoubleSortedMaps() {
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().charValue(), y.getKey().charValue());
   }

   public static ObjectBidirectionalIterator<Char2DoubleMap.Entry> fastIterator(Char2DoubleSortedMap map) {
      ObjectSortedSet<Char2DoubleMap.Entry> entries = map.char2DoubleEntrySet();
      return entries instanceof Char2DoubleSortedMap.FastSortedEntrySet
         ? ((Char2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Char2DoubleMap.Entry> fastIterable(Char2DoubleSortedMap map) {
      ObjectSortedSet<Char2DoubleMap.Entry> entries = map.char2DoubleEntrySet();
      return (ObjectBidirectionalIterable<Char2DoubleMap.Entry>)(entries instanceof Char2DoubleSortedMap.FastSortedEntrySet
         ? ((Char2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Char2DoubleSortedMap singleton(Character key, Double value) {
      return new Char2DoubleSortedMaps.Singleton(key, value);
   }

   public static Char2DoubleSortedMap singleton(Character key, Double value, CharComparator comparator) {
      return new Char2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2DoubleSortedMap singleton(char key, double value) {
      return new Char2DoubleSortedMaps.Singleton(key, value);
   }

   public static Char2DoubleSortedMap singleton(char key, double value, CharComparator comparator) {
      return new Char2DoubleSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2DoubleSortedMap synchronize(Char2DoubleSortedMap m) {
      return new Char2DoubleSortedMaps.SynchronizedSortedMap(m);
   }

   public static Char2DoubleSortedMap synchronize(Char2DoubleSortedMap m, Object sync) {
      return new Char2DoubleSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Char2DoubleSortedMap unmodifiable(Char2DoubleSortedMap m) {
      return new Char2DoubleSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Char2DoubleMaps.EmptyMap implements Char2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Double>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public Char2DoubleSortedMap subMap(char from, char to) {
         return Char2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2DoubleSortedMap headMap(char to) {
         return Char2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2DoubleSortedMap tailMap(char from) {
         return Char2DoubleSortedMaps.EMPTY_MAP;
      }

      @Override
      public char firstCharKey() {
         throw new NoSuchElementException();
      }

      @Override
      public char lastCharKey() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap subMap(Character ofrom, Character oto) {
         return this.subMap(ofrom.charValue(), oto.charValue());
      }

      @Deprecated
      @Override
      public Character firstKey() {
         return this.firstCharKey();
      }

      @Deprecated
      @Override
      public Character lastKey() {
         return this.lastCharKey();
      }
   }

   public static class Singleton extends Char2DoubleMaps.Singleton implements Char2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char key, double value, CharComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(char key, double value) {
         this(key, value, null);
      }

      final int compare(char k1, char k2) {
         return this.comparator == null ? Character.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public CharComparator comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractChar2DoubleMap.BasicEntry(this.key, this.value), Char2DoubleSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Char2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Double>> entrySet() {
         return this.char2DoubleEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2DoubleSortedMap subMap(char from, char to) {
         return (Char2DoubleSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Char2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2DoubleSortedMap headMap(char to) {
         return (Char2DoubleSortedMap)(this.compare(this.key, to) < 0 ? this : Char2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2DoubleSortedMap tailMap(char from) {
         return (Char2DoubleSortedMap)(this.compare(from, this.key) <= 0 ? this : Char2DoubleSortedMaps.EMPTY_MAP);
      }

      @Override
      public char firstCharKey() {
         return this.key;
      }

      @Override
      public char lastCharKey() {
         return this.key;
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap subMap(Character ofrom, Character oto) {
         return this.subMap(ofrom.charValue(), oto.charValue());
      }

      @Deprecated
      @Override
      public Character firstKey() {
         return this.firstCharKey();
      }

      @Deprecated
      @Override
      public Character lastKey() {
         return this.lastCharKey();
      }
   }

   public static class SynchronizedSortedMap extends Char2DoubleMaps.SynchronizedMap implements Char2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2DoubleSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2DoubleSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Char2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public CharComparator comparator() {
         synchronized (this.sync) {
            return this.sortedMap.comparator();
         }
      }

      @Override
      public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2DoubleEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Char2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Double>> entrySet() {
         return this.char2DoubleEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2DoubleSortedMap subMap(char from, char to) {
         return new Char2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Char2DoubleSortedMap headMap(char to) {
         return new Char2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Char2DoubleSortedMap tailMap(char from) {
         return new Char2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }

      @Override
      public char firstCharKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstCharKey();
         }
      }

      @Override
      public char lastCharKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastCharKey();
         }
      }

      @Deprecated
      @Override
      public Character firstKey() {
         synchronized (this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      @Deprecated
      @Override
      public Character lastKey() {
         synchronized (this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap subMap(Character from, Character to) {
         return new Char2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap headMap(Character to) {
         return new Char2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap tailMap(Character from) {
         return new Char2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Char2DoubleMaps.UnmodifiableMap implements Char2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2DoubleSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2DoubleSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2DoubleEntrySet());
         }

         return (ObjectSortedSet<Char2DoubleMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Double>> entrySet() {
         return this.char2DoubleEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2DoubleSortedMap subMap(char from, char to) {
         return new Char2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Char2DoubleSortedMap headMap(char to) {
         return new Char2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Char2DoubleSortedMap tailMap(char from) {
         return new Char2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }

      @Override
      public char firstCharKey() {
         return this.sortedMap.firstCharKey();
      }

      @Override
      public char lastCharKey() {
         return this.sortedMap.lastCharKey();
      }

      @Deprecated
      @Override
      public Character firstKey() {
         return this.sortedMap.firstKey();
      }

      @Deprecated
      @Override
      public Character lastKey() {
         return this.sortedMap.lastKey();
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap subMap(Character from, Character to) {
         return new Char2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap headMap(Character to) {
         return new Char2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Char2DoubleSortedMap tailMap(Character from) {
         return new Char2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
