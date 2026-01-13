package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class Char2ObjectSortedMaps {
   public static final Char2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Char2ObjectSortedMaps.EmptySortedMap();

   private Char2ObjectSortedMaps() {
   }

   public static Comparator<? super Entry<Character, ?>> entryComparator(CharComparator comparator) {
      return (x, y) -> comparator.compare(x.getKey().charValue(), y.getKey().charValue());
   }

   public static <V> ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> fastIterator(Char2ObjectSortedMap<V> map) {
      ObjectSortedSet<Char2ObjectMap.Entry<V>> entries = map.char2ObjectEntrySet();
      return entries instanceof Char2ObjectSortedMap.FastSortedEntrySet
         ? ((Char2ObjectSortedMap.FastSortedEntrySet)entries).fastIterator()
         : entries.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Char2ObjectMap.Entry<V>> fastIterable(Char2ObjectSortedMap<V> map) {
      ObjectSortedSet<Char2ObjectMap.Entry<V>> entries = map.char2ObjectEntrySet();
      return (ObjectBidirectionalIterable<Char2ObjectMap.Entry<V>>)(entries instanceof Char2ObjectSortedMap.FastSortedEntrySet
         ? ((Char2ObjectSortedMap.FastSortedEntrySet)entries)::fastIterator
         : entries);
   }

   public static <V> Char2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Char2ObjectSortedMap<V> singleton(Character key, V value) {
      return new Char2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Char2ObjectSortedMap<V> singleton(Character key, V value, CharComparator comparator) {
      return new Char2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Char2ObjectSortedMap<V> singleton(char key, V value) {
      return new Char2ObjectSortedMaps.Singleton<>(key, value);
   }

   public static <V> Char2ObjectSortedMap<V> singleton(char key, V value, CharComparator comparator) {
      return new Char2ObjectSortedMaps.Singleton<>(key, value, comparator);
   }

   public static <V> Char2ObjectSortedMap<V> synchronize(Char2ObjectSortedMap<V> m) {
      return new Char2ObjectSortedMaps.SynchronizedSortedMap<>(m);
   }

   public static <V> Char2ObjectSortedMap<V> synchronize(Char2ObjectSortedMap<V> m, Object sync) {
      return new Char2ObjectSortedMaps.SynchronizedSortedMap<>(m, sync);
   }

   public static <V> Char2ObjectSortedMap<V> unmodifiable(Char2ObjectSortedMap<? extends V> m) {
      return new Char2ObjectSortedMaps.UnmodifiableSortedMap<>(m);
   }

   public static class EmptySortedMap<V> extends Char2ObjectMaps.EmptyMap<V> implements Char2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet keySet() {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public Char2ObjectSortedMap<V> subMap(char from, char to) {
         return Char2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2ObjectSortedMap<V> headMap(char to) {
         return Char2ObjectSortedMaps.EMPTY_MAP;
      }

      @Override
      public Char2ObjectSortedMap<V> tailMap(char from) {
         return Char2ObjectSortedMaps.EMPTY_MAP;
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
      public Char2ObjectSortedMap<V> headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2ObjectSortedMap<V> tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2ObjectSortedMap<V> subMap(Character ofrom, Character oto) {
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

   public static class Singleton<V> extends Char2ObjectMaps.Singleton<V> implements Char2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharComparator comparator;

      protected Singleton(char key, V value, CharComparator comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(char key, V value) {
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
      public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(
               new AbstractChar2ObjectMap.BasicEntry<>(this.key, this.value), Char2ObjectSortedMaps.entryComparator(this.comparator)
            );
         }

         return (ObjectSortedSet<Char2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, V>> entrySet() {
         return this.char2ObjectEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.singleton(this.key, this.comparator);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ObjectSortedMap<V> subMap(char from, char to) {
         return (Char2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Char2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2ObjectSortedMap<V> headMap(char to) {
         return (Char2ObjectSortedMap<V>)(this.compare(this.key, to) < 0 ? this : Char2ObjectSortedMaps.EMPTY_MAP);
      }

      @Override
      public Char2ObjectSortedMap<V> tailMap(char from) {
         return (Char2ObjectSortedMap<V>)(this.compare(from, this.key) <= 0 ? this : Char2ObjectSortedMaps.EMPTY_MAP);
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
      public Char2ObjectSortedMap<V> headMap(Character oto) {
         return this.headMap(oto.charValue());
      }

      @Deprecated
      @Override
      public Char2ObjectSortedMap<V> tailMap(Character ofrom) {
         return this.tailMap(ofrom.charValue());
      }

      @Deprecated
      @Override
      public Char2ObjectSortedMap<V> subMap(Character ofrom, Character oto) {
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

   public static class SynchronizedSortedMap<V> extends Char2ObjectMaps.SynchronizedMap<V> implements Char2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Char2ObjectSortedMap<V> m, Object sync) {
         super(m, sync);
         this.sortedMap = m;
      }

      protected SynchronizedSortedMap(Char2ObjectSortedMap<V> m) {
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
      public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet<Char2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, V>> entrySet() {
         return this.char2ObjectEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ObjectSortedMap<V> subMap(char from, char to) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Override
      public Char2ObjectSortedMap<V> headMap(char to) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Override
      public Char2ObjectSortedMap<V> tailMap(char from) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
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
      public Char2ObjectSortedMap<V> subMap(Character from, Character to) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.subMap(from, to), this.sync);
      }

      @Deprecated
      @Override
      public Char2ObjectSortedMap<V> headMap(Character to) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.headMap(to), this.sync);
      }

      @Deprecated
      @Override
      public Char2ObjectSortedMap<V> tailMap(Character from) {
         return new Char2ObjectSortedMaps.SynchronizedSortedMap<>(this.sortedMap.tailMap(from), this.sync);
      }
   }

   public static class UnmodifiableSortedMap<V> extends Char2ObjectMaps.UnmodifiableMap<V> implements Char2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Char2ObjectSortedMap<? extends V> sortedMap;

      protected UnmodifiableSortedMap(Char2ObjectSortedMap<? extends V> m) {
         super(m);
         this.sortedMap = m;
      }

      @Override
      public CharComparator comparator() {
         return this.sortedMap.comparator();
      }

      @Override
      public ObjectSortedSet<Char2ObjectMap.Entry<V>> char2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable((ObjectSortedSet<Char2ObjectMap.Entry<V>>)this.sortedMap.char2ObjectEntrySet());
         }

         return (ObjectSortedSet<Char2ObjectMap.Entry<V>>)this.entries;
      }

      @Deprecated
      @Override
      public ObjectSortedSet<Entry<Character, V>> entrySet() {
         return this.char2ObjectEntrySet();
      }

      @Override
      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (CharSortedSet)this.keys;
      }

      @Override
      public Char2ObjectSortedMap<V> subMap(char from, char to) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Override
      public Char2ObjectSortedMap<V> headMap(char to) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Override
      public Char2ObjectSortedMap<V> tailMap(char from) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
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
      public Char2ObjectSortedMap<V> subMap(Character from, Character to) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.subMap(from, to));
      }

      @Deprecated
      @Override
      public Char2ObjectSortedMap<V> headMap(Character to) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.headMap(to));
      }

      @Deprecated
      @Override
      public Char2ObjectSortedMap<V> tailMap(Character from) {
         return new Char2ObjectSortedMaps.UnmodifiableSortedMap<>(this.sortedMap.tailMap(from));
      }
   }
}
