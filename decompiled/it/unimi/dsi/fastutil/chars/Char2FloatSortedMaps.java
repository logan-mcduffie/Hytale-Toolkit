package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Char2FloatSortedMaps {
   public static final Char2FloatSortedMaps.EmptySortedMap EMPTY_MAP = new Char2FloatSortedMaps.EmptySortedMap();

   private Char2FloatSortedMaps() {
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().charValue(), y.getKey().charValue());
   }

   public static ObjectBidirectionalIterator<Char2FloatMap.Entry> fastIterator(Char2FloatSortedMap map) {
      ObjectSortedSet<Char2FloatMap.Entry> entries = map.char2FloatEntrySet();
      return entries instanceof Char2FloatSortedMap.FastSortedEntrySet ? ((Char2FloatSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Char2FloatMap.Entry> fastIterable(Char2FloatSortedMap map) {
      ObjectSortedSet<Char2FloatMap.Entry> entries = map.char2FloatEntrySet();
      return (ObjectBidirectionalIterable<Char2FloatMap.Entry>)(entries instanceof Char2FloatSortedMap.FastSortedEntrySet
         ? ((Char2FloatSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Char2FloatSortedMap singleton(Character key, Float value) {
      return new Char2FloatSortedMaps.Singleton(key, value);
   }

   public static Char2FloatSortedMap singleton(Character key, Float value, CharComparator comparator) {
      return new Char2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2FloatSortedMap singleton(char key, float value) {
      return new Char2FloatSortedMaps.Singleton(key, value);
   }

   public static Char2FloatSortedMap singleton(char key, float value, CharComparator comparator) {
      return new Char2FloatSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2FloatSortedMap synchronize(Char2FloatSortedMap m) {
      return new Char2FloatSortedMaps.SynchronizedSortedMap(m);
   }

   public static Char2FloatSortedMap synchronize(Char2FloatSortedMap m, Object sync) {
      return new Char2FloatSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Char2FloatSortedMap unmodifiable(Char2FloatSortedMap m) {
      return new Char2FloatSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Char2FloatMaps.EmptyMap implements Char2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Float>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public Char2FloatSortedMap subMap(char from, char to) {
         return Char2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2FloatSortedMap headMap(char to) {
         return Char2FloatSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2FloatSortedMap tailMap(char from) {
         return Char2FloatSortedMaps.EMPTY_MAP;
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
      public Char2FloatSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2FloatSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2FloatSortedMap subMap(Character ofrom, Character oto) {
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

   public static class Singleton extends Char2FloatMaps.Singleton implements Char2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char key, float value, CharComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(char key, float value) {
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
      public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractChar2FloatMap.BasicEntry(this.key, this.value), Char2FloatSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Char2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2FloatSortedMap subMap(char from, char to) {
         return (Char2FloatSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Char2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2FloatSortedMap headMap(char to) {
         return (Char2FloatSortedMap)(this.compare(this.key, to) < 0 ? this : Char2FloatSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2FloatSortedMap tailMap(char from) {
         return (Char2FloatSortedMap)(this.compare(from, this.key) <= 0 ? this : Char2FloatSortedMaps.EMPTY_MAP);
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
      public Char2FloatSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2FloatSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2FloatSortedMap subMap(Character ofrom, Character oto) {
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

   public static class SynchronizedSortedMap extends Char2FloatMaps.SynchronizedMap implements Char2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2FloatSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2FloatSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Char2FloatSortedMap m) {
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
      public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2FloatEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Char2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2FloatSortedMap subMap(char from, char to) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Char2FloatSortedMap headMap(char to) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Char2FloatSortedMap tailMap(char from) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Char2FloatSortedMap subMap(Character from, Character to) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Char2FloatSortedMap headMap(Character to) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Char2FloatSortedMap tailMap(Character from) {
         return new Char2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Char2FloatMaps.UnmodifiableMap implements Char2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2FloatSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2FloatSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Char2FloatMap.Entry> char2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2FloatEntrySet());
         }

         return (ObjectSortedSet<Char2FloatMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Float>> entrySet() {
         return this.char2FloatEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2FloatSortedMap subMap(char from, char to) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Char2FloatSortedMap headMap(char to) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Char2FloatSortedMap tailMap(char from) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Char2FloatSortedMap subMap(Character from, Character to) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Char2FloatSortedMap headMap(Character to) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Char2FloatSortedMap tailMap(Character from) {
         return new Char2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
