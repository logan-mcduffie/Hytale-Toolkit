package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.NoSuchElementException;

public final class CharSortedSets {
   public static final CharSortedSets.EmptySet EMPTY_SET = new CharSortedSets.EmptySet();

   private CharSortedSets() {
   }

   public static CharSortedSet singleton(char element) {
      return new CharSortedSets.Singleton(element);
   }

   public static CharSortedSet singleton(char element, CharComparator comparator) {
      return new CharSortedSets.Singleton(element, comparator);
   }

   public static CharSortedSet singleton(Object element) {
      return new CharSortedSets.Singleton((Character)element);
   }

   public static CharSortedSet singleton(Object element, CharComparator comparator) {
      return new CharSortedSets.Singleton((Character)element, comparator);
   }

   public static CharSortedSet synchronize(CharSortedSet s) {
      return new CharSortedSets.SynchronizedSortedSet(s);
   }

   public static CharSortedSet synchronize(CharSortedSet s, Object sync) {
      return new CharSortedSets.SynchronizedSortedSet(s, sync);
   }

   public static CharSortedSet unmodifiable(CharSortedSet s) {
      return new CharSortedSets.UnmodifiableSortedSet(s);
   }

   public static class EmptySet extends CharSets.EmptySet implements CharSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public CharBidirectionalIterator iterator(char from) {
         return CharIterators.EMPTY_ITERATOR;
      }

      @Override
      public CharSortedSet subSet(char from, char to) {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet headSet(char from) {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public CharSortedSet tailSet(char to) {
         return CharSortedSets.EMPTY_SET;
      }

      @Override
      public char firstChar() {
         throw new NoSuchElementException();
      }

      @Override
      public char lastChar() {
         throw new NoSuchElementException();
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Deprecated
      @Override
      public CharSortedSet subSet(Character from, Character to) {
         return CharSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public CharSortedSet headSet(Character from) {
         return CharSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public CharSortedSet tailSet(Character to) {
         return CharSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public Character first() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Character last() {
         throw new NoSuchElementException();
      }

      @Override
      public Object clone() {
         return CharSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return CharSortedSets.EMPTY_SET;
      }
   }

   public static class Singleton extends CharSets.Singleton implements CharSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final CharComparator comparator;

      protected Singleton(char element, CharComparator comparator) {
         super(element);
         this.comparator = comparator;
      }

      Singleton(char element) {
         this(element, null);
      }

      final int compare(char k1, char k2) {
         return this.comparator == null ? Character.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public CharBidirectionalIterator iterator(char from) {
         CharBidirectionalIterator i = this.iterator();
         if (this.compare(this.element, from) <= 0) {
            i.nextChar();
         }

         return i;
      }

      @Override
      public CharComparator comparator() {
         return this.comparator;
      }

      @Override
      public CharSpliterator spliterator() {
         return CharSpliterators.singleton(this.element, this.comparator);
      }

      @Override
      public CharSortedSet subSet(char from, char to) {
         return (CharSortedSet)(this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0 ? this : CharSortedSets.EMPTY_SET);
      }

      @Override
      public CharSortedSet headSet(char to) {
         return (CharSortedSet)(this.compare(this.element, to) < 0 ? this : CharSortedSets.EMPTY_SET);
      }

      @Override
      public CharSortedSet tailSet(char from) {
         return (CharSortedSet)(this.compare(from, this.element) <= 0 ? this : CharSortedSets.EMPTY_SET);
      }

      @Override
      public char firstChar() {
         return this.element;
      }

      @Override
      public char lastChar() {
         return this.element;
      }

      @Override
      public IntSpliterator intSpliterator() {
         return IntSpliterators.singleton(this.element, (left, right) -> this.comparator().compare(SafeMath.safeIntToChar(left), SafeMath.safeIntToChar(right)));
      }

      @Deprecated
      @Override
      public CharSortedSet subSet(Character from, Character to) {
         return this.subSet(from.charValue(), to.charValue());
      }

      @Deprecated
      @Override
      public CharSortedSet headSet(Character to) {
         return this.headSet(to.charValue());
      }

      @Deprecated
      @Override
      public CharSortedSet tailSet(Character from) {
         return this.tailSet(from.charValue());
      }

      @Deprecated
      @Override
      public Character first() {
         return this.element;
      }

      @Deprecated
      @Override
      public Character last() {
         return this.element;
      }
   }

   public static class SynchronizedSortedSet extends CharSets.SynchronizedSet implements CharSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharSortedSet sortedSet;

      protected SynchronizedSortedSet(CharSortedSet s, Object sync) {
         super(s, sync);
         this.sortedSet = s;
      }

      protected SynchronizedSortedSet(CharSortedSet s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public CharComparator comparator() {
         synchronized (this.sync) {
            return this.sortedSet.comparator();
         }
      }

      @Override
      public CharSortedSet subSet(char from, char to) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
      }

      @Override
      public CharSortedSet headSet(char to) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
      }

      @Override
      public CharSortedSet tailSet(char from) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
      }

      @Override
      public CharBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      @Override
      public CharBidirectionalIterator iterator(char from) {
         return this.sortedSet.iterator(from);
      }

      @Override
      public char firstChar() {
         synchronized (this.sync) {
            return this.sortedSet.firstChar();
         }
      }

      @Override
      public char lastChar() {
         synchronized (this.sync) {
            return this.sortedSet.lastChar();
         }
      }

      @Deprecated
      @Override
      public Character first() {
         synchronized (this.sync) {
            return this.sortedSet.first();
         }
      }

      @Deprecated
      @Override
      public Character last() {
         synchronized (this.sync) {
            return this.sortedSet.last();
         }
      }

      @Deprecated
      @Override
      public CharSortedSet subSet(Character from, Character to) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
      }

      @Deprecated
      @Override
      public CharSortedSet headSet(Character to) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
      }

      @Deprecated
      @Override
      public CharSortedSet tailSet(Character from) {
         return new CharSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
      }
   }

   public static class UnmodifiableSortedSet extends CharSets.UnmodifiableSet implements CharSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharSortedSet sortedSet;

      protected UnmodifiableSortedSet(CharSortedSet s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public CharComparator comparator() {
         return this.sortedSet.comparator();
      }

      @Override
      public CharSortedSet subSet(char from, char to) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
      }

      @Override
      public CharSortedSet headSet(char to) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(to));
      }

      @Override
      public CharSortedSet tailSet(char from) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(from));
      }

      @Override
      public CharBidirectionalIterator iterator() {
         return CharIterators.unmodifiable(this.sortedSet.iterator());
      }

      @Override
      public CharBidirectionalIterator iterator(char from) {
         return CharIterators.unmodifiable(this.sortedSet.iterator(from));
      }

      @Override
      public char firstChar() {
         return this.sortedSet.firstChar();
      }

      @Override
      public char lastChar() {
         return this.sortedSet.lastChar();
      }

      @Deprecated
      @Override
      public Character first() {
         return this.sortedSet.first();
      }

      @Deprecated
      @Override
      public Character last() {
         return this.sortedSet.last();
      }

      @Deprecated
      @Override
      public CharSortedSet subSet(Character from, Character to) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
      }

      @Deprecated
      @Override
      public CharSortedSet headSet(Character to) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(to));
      }

      @Deprecated
      @Override
      public CharSortedSet tailSet(Character from) {
         return new CharSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(from));
      }
   }
}
