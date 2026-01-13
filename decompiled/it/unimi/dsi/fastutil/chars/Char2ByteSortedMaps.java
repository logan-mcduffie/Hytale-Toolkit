package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Char2ByteSortedMaps {
   public static final Char2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Char2ByteSortedMaps.EmptySortedMap();

   private Char2ByteSortedMaps() {
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().charValue(), y.getKey().charValue());
   }

   public static ObjectBidirectionalIterator<Char2ByteMap.Entry> fastIterator(Char2ByteSortedMap map) {
      ObjectSortedSet<Char2ByteMap.Entry> entries = map.char2ByteEntrySet();
      return entries instanceof Char2ByteSortedMap.FastSortedEntrySet ? ((Char2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static ObjectBidirectionalIterable<Char2ByteMap.Entry> fastIterable(Char2ByteSortedMap map) {
      ObjectSortedSet<Char2ByteMap.Entry> entries = map.char2ByteEntrySet();
      return (ObjectBidirectionalIterable<Char2ByteMap.Entry>)(entries instanceof Char2ByteSortedMap.FastSortedEntrySet
         ? ((Char2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static Char2ByteSortedMap singleton(Character key, Byte value) {
      return new Char2ByteSortedMaps.Singleton(key, value);
   }

   public static Char2ByteSortedMap singleton(Character key, Byte value, CharComparator comparator) {
      return new Char2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2ByteSortedMap singleton(char key, byte value) {
      return new Char2ByteSortedMaps.Singleton(key, value);
   }

   public static Char2ByteSortedMap singleton(char key, byte value, CharComparator comparator) {
      return new Char2ByteSortedMaps.Singleton(key, value, comparator);
   }

   public static Char2ByteSortedMap synchronize(Char2ByteSortedMap m) {
      return new Char2ByteSortedMaps.SynchronizedSortedMap(m);
   }

   public static Char2ByteSortedMap synchronize(Char2ByteSortedMap m, Object sync) {
      return new Char2ByteSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static Char2ByteSortedMap unmodifiable(Char2ByteSortedMap m) {
      return new Char2ByteSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap extends Char2ByteMaps.EmptyMap implements Char2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public Char2ByteSortedMap subMap(char from, char to) {
         return Char2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2ByteSortedMap headMap(char to) {
         return Char2ByteSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2ByteSortedMap tailMap(char from) {
         return Char2ByteSortedMaps.EMPTY_MAP;
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
      public Char2ByteSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2ByteSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2ByteSortedMap subMap(Character ofrom, Character oto) {
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

   public static class Singleton extends Char2ByteMaps.Singleton implements Char2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char key, byte value, CharComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(char key, byte value) {
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
      public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractChar2ByteMap.BasicEntry(this.key, this.value), Char2ByteSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Char2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ByteSortedMap subMap(char from, char to) {
         return (Char2ByteSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Char2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2ByteSortedMap headMap(char to) {
         return (Char2ByteSortedMap)(this.compare(this.key, to) < 0 ? this : Char2ByteSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2ByteSortedMap tailMap(char from) {
         return (Char2ByteSortedMap)(this.compare(from, this.key) <= 0 ? this : Char2ByteSortedMaps.EMPTY_MAP);
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
      public Char2ByteSortedMap headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2ByteSortedMap tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2ByteSortedMap subMap(Character ofrom, Character oto) {
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

   public static class SynchronizedSortedMap extends Char2ByteMaps.SynchronizedMap implements Char2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ByteSortedMap sortedMap;

      protected SynchronizedSortedMap(Char2ByteSortedMap m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Char2ByteSortedMap m) {
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
      public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Char2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ByteSortedMap subMap(char from, char to) {
         return new Char2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Char2ByteSortedMap headMap(char to) {
         return new Char2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Char2ByteSortedMap tailMap(char from) {
         return new Char2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
      public Char2ByteSortedMap subMap(Character from, Character to) {
         return new Char2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Char2ByteSortedMap headMap(Character to) {
         return new Char2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Char2ByteSortedMap tailMap(Character from) {
         return new Char2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap extends Char2ByteMaps.UnmodifiableMap implements Char2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ByteSortedMap sortedMap;

      protected UnmodifiableSortedMap(Char2ByteSortedMap m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2ByteEntrySet());
         }

         return (ObjectSortedSet<Char2ByteMap.Entry>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, Byte>> entrySet() {
         return this.char2ByteEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ByteSortedMap subMap(char from, char to) {
         return new Char2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Override
      public Char2ByteSortedMap headMap(char to) {
         return new Char2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Override
      public Char2ByteSortedMap tailMap(char from) {
         return new Char2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
      public Char2ByteSortedMap subMap(Character from, Character to) {
         return new Char2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Char2ByteSortedMap headMap(Character to) {
         return new Char2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Char2ByteSortedMap tailMap(Character from) {
         return new Char2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(from));
      }
   }
}
