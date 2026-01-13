package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Char2IntSortedMaps {
   public static final Char2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Char2IntSortedMaps.EmptySortedMap();

   private Char2IntSortedMaps() {
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().charValue(), y.getKey().charValue());
   }

   public static ObjectBidirectionalIterator<Char2IntMap.Entry> fastIterator(Char2IntSortedMap map) {
      ObjectSortedSet<Char2IntMap.Entry> entries = map.char2IntEntrySet();
      return entries instanceof Char2IntSortedMap.FastSortedEntrySet ? ((Char2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Char2IntMap.Entry> fastIterable(Char2IntSortedMap map) {
      ObjectSortedSet<Char2IntMap.Entry> entries = map.char2IntEntrySet();
      return (ObjectBidirectionalIterable<Char2IntMap.Entry>)(entries instanceof Char2IntSortedMap.FastSortedEntrySet
         ? ((Char2IntSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Char2IntSortedMap singleton(Character key, Integer value) {
      return new Char2IntSortedMaps.Singleton(key, value);
   }

   public static Char2IntSortedMap singleton(Character key, Integer value, CharComparator comparator) {
      return new Char2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2IntSortedMap singleton(char key, int value) {
      return new Char2IntSortedMaps.Singleton(key, value);
   }

   public static Char2IntSortedMap singleton(char key, int value, CharComparator comparator) {
      return new Char2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2IntSortedMap synchronize(Char2IntSortedMap m) {
      return new Char2IntSortedMaps.SynchronizedSortedMap(m);
   }

   public static Char2IntSortedMap synchronize(Char2IntSortedMap m, Object sync) {
      return new Char2IntSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Char2IntSortedMap unmodifiable(Char2IntSortedMap m) {
      return new Char2IntSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Char2IntMaps.EmptyMap implements Char2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public Char2IntSortedMap subMap(char from, char to) {
         return Char2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2IntSortedMap headMap(char to) {
         return Char2IntSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2IntSortedMap tailMap(char from) {
         return Char2IntSortedMaps.EMPTY_MAP;
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
      public Char2IntSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2IntSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2IntSortedMap subMap(Character ofrom, Character oto) {
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

   public static class Singleton extends Char2IntMaps.Singleton implements Char2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char key, int value, CharComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(char key, int value) {
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
      public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractChar2IntMap.BasicEntry(this.key, this.value), Char2IntSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Char2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Integer>> entrySet() {
         return this.char2IntEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2IntSortedMap subMap(char from, char to) {
         return (Char2IntSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Char2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2IntSortedMap headMap(char to) {
         return (Char2IntSortedMap)(this.compare(this.key, to) < 0 ? this : Char2IntSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2IntSortedMap tailMap(char from) {
         return (Char2IntSortedMap)(this.compare(from, this.key) <= 0 ? this : Char2IntSortedMaps.EMPTY_MAP);
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
      public Char2IntSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2IntSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2IntSortedMap subMap(Character ofrom, Character oto) {
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

   public static class SynchronizedSortedMap extends Char2IntMaps.SynchronizedMap implements Char2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2IntSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2IntSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Char2IntSortedMap m) {
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
      public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Char2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Integer>> entrySet() {
         return this.char2IntEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2IntSortedMap subMap(char from, char to) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Char2IntSortedMap headMap(char to) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Char2IntSortedMap tailMap(char from) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Char2IntSortedMap subMap(Character from, Character to) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Char2IntSortedMap headMap(Character to) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Char2IntSortedMap tailMap(Character from) {
         return new Char2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Char2IntMaps.UnmodifiableMap implements Char2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2IntSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2IntSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Char2IntMap.Entry> char2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2IntEntrySet());
         }

         return (ObjectSortedSet<Char2IntMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Integer>> entrySet() {
         return this.char2IntEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2IntSortedMap subMap(char from, char to) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Char2IntSortedMap headMap(char to) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Char2IntSortedMap tailMap(char from) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Char2IntSortedMap subMap(Character from, Character to) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Char2IntSortedMap headMap(Character to) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Char2IntSortedMap tailMap(Character from) {
         return new Char2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
