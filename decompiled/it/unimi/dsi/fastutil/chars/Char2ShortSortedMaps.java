package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Char2ShortSortedMaps {
   public static final Char2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Char2ShortSortedMaps.EmptySortedMap();

   private Char2ShortSortedMaps() {
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().charValue(), y.getKey().charValue());
   }

   public static ObjectBidirectionalIterator<Char2ShortMap.Entry> fastIterator(Char2ShortSortedMap map) {
      ObjectSortedSet<Char2ShortMap.Entry> entries = map.char2ShortEntrySet();
      return entries instanceof Char2ShortSortedMap.FastSortedEntrySet ? ((Char2ShortSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Char2ShortMap.Entry> fastIterable(Char2ShortSortedMap map) {
      ObjectSortedSet<Char2ShortMap.Entry> entries = map.char2ShortEntrySet();
      return (ObjectBidirectionalIterable<Char2ShortMap.Entry>)(entries instanceof Char2ShortSortedMap.FastSortedEntrySet
         ? ((Char2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Char2ShortSortedMap singleton(Character key, Short value) {
      return new Char2ShortSortedMaps.Singleton(key, value);
   }

   public static Char2ShortSortedMap singleton(Character key, Short value, CharComparator comparator) {
      return new Char2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2ShortSortedMap singleton(char key, short value) {
      return new Char2ShortSortedMaps.Singleton(key, value);
   }

   public static Char2ShortSortedMap singleton(char key, short value, CharComparator comparator) {
      return new Char2ShortSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2ShortSortedMap synchronize(Char2ShortSortedMap m) {
      return new Char2ShortSortedMaps.SynchronizedSortedMap(m);
   }

   public static Char2ShortSortedMap synchronize(Char2ShortSortedMap m, Object sync) {
      return new Char2ShortSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Char2ShortSortedMap unmodifiable(Char2ShortSortedMap m) {
      return new Char2ShortSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Char2ShortMaps.EmptyMap implements Char2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Char2ShortMap.Entry> char2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public Char2ShortSortedMap subMap(char from, char to) {
         return Char2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2ShortSortedMap headMap(char to) {
         return Char2ShortSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2ShortSortedMap tailMap(char from) {
         return Char2ShortSortedMaps.EMPTY_MAP;
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
      public Char2ShortSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2ShortSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2ShortSortedMap subMap(Character ofrom, Character oto) {
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

   public static class Singleton extends Char2ShortMaps.Singleton implements Char2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char key, short value, CharComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(char key, short value) {
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
      public ObjectSortedSet<Char2ShortMap.Entry> char2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractChar2ShortMap.BasicEntry(this.key, this.value), Char2ShortSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Char2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Short>> entrySet() {
         return this.char2ShortEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ShortSortedMap subMap(char from, char to) {
         return (Char2ShortSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Char2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2ShortSortedMap headMap(char to) {
         return (Char2ShortSortedMap)(this.compare(this.key, to) < 0 ? this : Char2ShortSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2ShortSortedMap tailMap(char from) {
         return (Char2ShortSortedMap)(this.compare(from, this.key) <= 0 ? this : Char2ShortSortedMaps.EMPTY_MAP);
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
      public Char2ShortSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2ShortSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2ShortSortedMap subMap(Character ofrom, Character oto) {
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

   public static class SynchronizedSortedMap extends Char2ShortMaps.SynchronizedMap implements Char2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ShortSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2ShortSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Char2ShortSortedMap m) {
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
      public ObjectSortedSet<Char2ShortMap.Entry> char2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Char2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Short>> entrySet() {
         return this.char2ShortEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ShortSortedMap subMap(char from, char to) {
         return new Char2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Char2ShortSortedMap headMap(char to) {
         return new Char2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Char2ShortSortedMap tailMap(char from) {
         return new Char2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Char2ShortSortedMap subMap(Character from, Character to) {
         return new Char2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Char2ShortSortedMap headMap(Character to) {
         return new Char2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Char2ShortSortedMap tailMap(Character from) {
         return new Char2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Char2ShortMaps.UnmodifiableMap implements Char2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ShortSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2ShortSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Char2ShortMap.Entry> char2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2ShortEntrySet());
         }

         return (ObjectSortedSet<Char2ShortMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Short>> entrySet() {
         return this.char2ShortEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ShortSortedMap subMap(char from, char to) {
         return new Char2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Char2ShortSortedMap headMap(char to) {
         return new Char2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Char2ShortSortedMap tailMap(char from) {
         return new Char2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Char2ShortSortedMap subMap(Character from, Character to) {
         return new Char2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Char2ShortSortedMap headMap(Character to) {
         return new Char2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Char2ShortSortedMap tailMap(Character from) {
         return new Char2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
