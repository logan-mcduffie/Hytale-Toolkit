package it.unimi.dsi.fastutil.ints;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class IntSortedSets {
   public static final IntSortedSets.EmptySet EMPTY_SET = new IntSortedSets.EmptySet();

   private IntSortedSets() {
   }

   public static IntSortedSet singleton(int element) {
      return new IntSortedSets.Singleton(element);
   }

   public static IntSortedSet singleton(int element, IntComparator comparator) {
      return new IntSortedSets.Singleton(element, comparator);
   }

   public static IntSortedSet singleton(Object element) {
      return new IntSortedSets.Singleton((Integer)element);
   }

   public static IntSortedSet singleton(Object element, IntComparator comparator) {
      return new IntSortedSets.Singleton((Integer)element, comparator);
   }

   public static IntSortedSet synchronize(IntSortedSet s) {
      return new IntSortedSets.SynchronizedSortedSet(s);
   }

   public static IntSortedSet synchronize(IntSortedSet s, Object sync) {
      return new IntSortedSets.SynchronizedSortedSet(s, sync);
   }

   public static IntSortedSet unmodifiable(IntSortedSet s) {
      return new IntSortedSets.UnmodifiableSortedSet(s);
   }

   public static class EmptySet extends IntSets.EmptySet implements IntSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return IntIterators.EMPTY_ITERATOR;
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public IntSortedSet headSet(int from) {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public IntSortedSet tailSet(int to) {
         return IntSortedSets.EMPTY_SET;
      }

      @Override
      public int firstInt() {
         throw new NoSuchElementException();
      }

      @Override
      public int lastInt() {
         throw new NoSuchElementException();
      }

      @Override
      public IntComparator comparator() {
         return null;
      }

      @Deprecated
      @Override
      public IntSortedSet subSet(Integer from, Integer to) {
         return IntSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public IntSortedSet headSet(Integer from) {
         return IntSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public IntSortedSet tailSet(Integer to) {
         return IntSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public Integer first() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Integer last() {
         throw new NoSuchElementException();
      }

      @Override
      public Object clone() {
         return IntSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return IntSortedSets.EMPTY_SET;
      }
   }

   public static class Singleton extends IntSets.Singleton implements IntSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final IntComparator comparator;

      protected Singleton(int element, IntComparator comparator) {
         super(element);
         this.comparator = comparator;
      }

      Singleton(int element) {
         this(element, null);
      }

      final int compare(int k1, int k2) {
         return this.comparator == null ? Integer.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         IntBidirectionalIterator i = this.iterator();
         if (this.compare(this.element, from) <= 0) {
            i.nextInt();
         }

         return i;
      }

      @Override
      public IntComparator comparator() {
         return this.comparator;
      }

      @Override
      public IntSpliterator spliterator() {
         return IntSpliterators.singleton(this.element, this.comparator);
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return (IntSortedSet)(this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0 ? this : IntSortedSets.EMPTY_SET);
      }

      @Override
      public IntSortedSet headSet(int to) {
         return (IntSortedSet)(this.compare(this.element, to) < 0 ? this : IntSortedSets.EMPTY_SET);
      }

      @Override
      public IntSortedSet tailSet(int from) {
         return (IntSortedSet)(this.compare(from, this.element) <= 0 ? this : IntSortedSets.EMPTY_SET);
      }

      @Override
      public int firstInt() {
         return this.element;
      }

      @Override
      public int lastInt() {
         return this.element;
      }

      @Deprecated
      @Override
      public IntSortedSet subSet(Integer from, Integer to) {
         return this.subSet(from.intValue(), to.intValue());
      }

      @Deprecated
      @Override
      public IntSortedSet headSet(Integer to) {
         return this.headSet(to.intValue());
      }

      @Deprecated
      @Override
      public IntSortedSet tailSet(Integer from) {
         return this.tailSet(from.intValue());
      }

      @Deprecated
      @Override
      public Integer first() {
         return this.element;
      }

      @Deprecated
      @Override
      public Integer last() {
         return this.element;
      }
   }

   public static class SynchronizedSortedSet extends IntSets.SynchronizedSet implements IntSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntSortedSet sortedSet;

      protected SynchronizedSortedSet(IntSortedSet s, Object sync) {
         super(s, sync);
         this.sortedSet = s;
      }

      protected SynchronizedSortedSet(IntSortedSet s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public IntComparator comparator() {
         synchronized (this.sync) {
            return this.sortedSet.comparator();
         }
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
      }

      @Override
      public IntSortedSet headSet(int to) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
      }

      @Override
      public IntSortedSet tailSet(int from) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return this.sortedSet.iterator(from);
      }

      @Override
      public int firstInt() {
         synchronized (this.sync) {
            return this.sortedSet.firstInt();
         }
      }

      @Override
      public int lastInt() {
         synchronized (this.sync) {
            return this.sortedSet.lastInt();
         }
      }

      @Deprecated
      @Override
      public Integer first() {
         synchronized (this.sync) {
            return this.sortedSet.first();
         }
      }

      @Deprecated
      @Override
      public Integer last() {
         synchronized (this.sync) {
            return this.sortedSet.last();
         }
      }

      @Deprecated
      @Override
      public IntSortedSet subSet(Integer from, Integer to) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
      }

      @Deprecated
      @Override
      public IntSortedSet headSet(Integer to) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
      }

      @Deprecated
      @Override
      public IntSortedSet tailSet(Integer from) {
         return new IntSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
      }
   }

   public static class UnmodifiableSortedSet extends IntSets.UnmodifiableSet implements IntSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntSortedSet sortedSet;

      protected UnmodifiableSortedSet(IntSortedSet s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public IntComparator comparator() {
         return this.sortedSet.comparator();
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
      }

      @Override
      public IntSortedSet headSet(int to) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(to));
      }

      @Override
      public IntSortedSet tailSet(int from) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(from));
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return IntIterators.unmodifiable(this.sortedSet.iterator());
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return IntIterators.unmodifiable(this.sortedSet.iterator(from));
      }

      @Override
      public int firstInt() {
         return this.sortedSet.firstInt();
      }

      @Override
      public int lastInt() {
         return this.sortedSet.lastInt();
      }

      @Deprecated
      @Override
      public Integer first() {
         return this.sortedSet.first();
      }

      @Deprecated
      @Override
      public Integer last() {
         return this.sortedSet.last();
      }

      @Deprecated
      @Override
      public IntSortedSet subSet(Integer from, Integer to) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
      }

      @Deprecated
      @Override
      public IntSortedSet headSet(Integer to) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(to));
      }

      @Deprecated
      @Override
      public IntSortedSet tailSet(Integer from) {
         return new IntSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(from));
      }
   }
}
