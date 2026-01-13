package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Char2CharSortedMaps {
   public static final Char2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Char2CharSortedMaps.EmptySortedMap();

   private Char2CharSortedMaps() {
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().charValue(), y.getKey().charValue());
   }

   public static ObjectBidirectionalIterator<Char2CharMap.Entry> fastIterator(Char2CharSortedMap map) {
      ObjectSortedSet<Char2CharMap.Entry> entries = map.char2CharEntrySet();
      return entries instanceof Char2CharSortedMap.FastSortedEntrySet ? ((Char2CharSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Char2CharMap.Entry> fastIterable(Char2CharSortedMap map) {
      ObjectSortedSet<Char2CharMap.Entry> entries = map.char2CharEntrySet();
      return (ObjectBidirectionalIterable<Char2CharMap.Entry>)(entries instanceof Char2CharSortedMap.FastSortedEntrySet
         ? ((Char2CharSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Char2CharSortedMap singleton(Character key, Character value) {
      return new Char2CharSortedMaps.Singleton(key, value);
   }

   public static Char2CharSortedMap singleton(Character key, Character value, CharComparator comparator) {
      return new Char2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2CharSortedMap singleton(char key, char value) {
      return new Char2CharSortedMaps.Singleton(key, value);
   }

   public static Char2CharSortedMap singleton(char key, char value, CharComparator comparator) {
      return new Char2CharSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2CharSortedMap synchronize(Char2CharSortedMap m) {
      return new Char2CharSortedMaps.SynchronizedSortedMap(m);
   }

   public static Char2CharSortedMap synchronize(Char2CharSortedMap m, Object sync) {
      return new Char2CharSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Char2CharSortedMap unmodifiable(Char2CharSortedMap m) {
      return new Char2CharSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Char2CharMaps.EmptyMap implements Char2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Char2CharMap.Entry> char2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public Char2CharSortedMap subMap(char from, char to) {
         return Char2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2CharSortedMap headMap(char to) {
         return Char2CharSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2CharSortedMap tailMap(char from) {
         return Char2CharSortedMaps.EMPTY_MAP;
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
      public Char2CharSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2CharSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2CharSortedMap subMap(Character ofrom, Character oto) {
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

   public static class Singleton extends Char2CharMaps.Singleton implements Char2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char key, char value, CharComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(char key, char value) {
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
      public ObjectSortedSet<Char2CharMap.Entry> char2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractChar2CharMap.BasicEntry(this.key, this.value), Char2CharSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Char2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2CharSortedMap subMap(char from, char to) {
         return (Char2CharSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Char2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2CharSortedMap headMap(char to) {
         return (Char2CharSortedMap)(this.compare(this.key, to) < 0 ? this : Char2CharSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2CharSortedMap tailMap(char from) {
         return (Char2CharSortedMap)(this.compare(from, this.key) <= 0 ? this : Char2CharSortedMaps.EMPTY_MAP);
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
      public Char2CharSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2CharSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2CharSortedMap subMap(Character ofrom, Character oto) {
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

   public static class SynchronizedSortedMap extends Char2CharMaps.SynchronizedMap implements Char2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2CharSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Char2CharSortedMap m) {
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
      public ObjectSortedSet<Char2CharMap.Entry> char2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Char2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2CharSortedMap subMap(char from, char to) {
         return new Char2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Char2CharSortedMap headMap(char to) {
         return new Char2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Char2CharSortedMap tailMap(char from) {
         return new Char2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Char2CharSortedMap subMap(Character from, Character to) {
         return new Char2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Char2CharSortedMap headMap(Character to) {
         return new Char2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Char2CharSortedMap tailMap(Character from) {
         return new Char2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Char2CharMaps.UnmodifiableMap implements Char2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2CharSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Char2CharMap.Entry> char2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2CharEntrySet());
         }

         return (ObjectSortedSet<Char2CharMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Character>> entrySet() {
         return this.char2CharEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2CharSortedMap subMap(char from, char to) {
         return new Char2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Char2CharSortedMap headMap(char to) {
         return new Char2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Char2CharSortedMap tailMap(char from) {
         return new Char2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Char2CharSortedMap subMap(Character from, Character to) {
         return new Char2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Char2CharSortedMap headMap(Character to) {
         return new Char2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Char2CharSortedMap tailMap(Character from) {
         return new Char2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
