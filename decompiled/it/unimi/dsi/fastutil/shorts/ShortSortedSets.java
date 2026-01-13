package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.NoSuchElementException;

public final class ShortSortedSets {
   public static final ShortSortedSets.EmptySet EMPTY_SET = new ShortSortedSets.EmptySet();

   private ShortSortedSets() {
   }

   public static ShortSortedSet singleton(short element) {
      return new ShortSortedSets.Singleton(element);
   }

   public static ShortSortedSet singleton(short element, ShortComparator comparator) {
      return new ShortSortedSets.Singleton(element, comparator);
   }

   public static ShortSortedSet singleton(Object element) {
      return new ShortSortedSets.Singleton((Short)element);
   }

   public static ShortSortedSet singleton(Object element, ShortComparator comparator) {
      return new ShortSortedSets.Singleton((Short)element, comparator);
   }

   public static ShortSortedSet synchronize(ShortSortedSet s) {
      return new ShortSortedSets.SynchronizedSortedSet(s);
   }

   public static ShortSortedSet synchronize(ShortSortedSet s, Object sync) {
      return new ShortSortedSets.SynchronizedSortedSet(s, sync);
   }

   public static ShortSortedSet unmodifiable(ShortSortedSet s) {
      return new ShortSortedSets.UnmodifiableSortedSet(s);
   }

   public static class EmptySet extends ShortSets.EmptySet implements ShortSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public ShortBidirectionalIterator iterator(short from) {
         return ShortIterators.EMPTY_ITERATOR;
      }

      @Override
      public ShortSortedSet subSet(short from, short to) {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet headSet(short from) {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public ShortSortedSet tailSet(short to) {
         return ShortSortedSets.EMPTY_SET;
      }

      @Override
      public short firstShort() {
         throw new NoSuchElementException();
      }

      @Override
      public short lastShort() {
         throw new NoSuchElementException();
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Deprecated
      @Override
      public ShortSortedSet subSet(Short from, Short to) {
         return ShortSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ShortSortedSet headSet(Short from) {
         return ShortSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public ShortSortedSet tailSet(Short to) {
         return ShortSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public Short first() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Short last() {
         throw new NoSuchElementException();
      }

      @Override
      public Object clone() {
         return ShortSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return ShortSortedSets.EMPTY_SET;
      }
   }

   public static class Singleton extends ShortSets.Singleton implements ShortSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final ShortComparator comparator;

      protected Singleton(short element, ShortComparator comparator) {
         super(element);
         this.comparator = comparator;
      }

      Singleton(short element) {
         this(element, null);
      }

      final int compare(short k1, short k2) {
         return this.comparator == null ? Short.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public ShortBidirectionalIterator iterator(short from) {
         ShortBidirectionalIterator i = this.iterator();
         if (this.compare(this.element, from) <= 0) {
            i.nextShort();
         }

         return i;
      }

      @Override
      public ShortComparator comparator() {
         return this.comparator;
      }

      @Override
      public ShortSpliterator spliterator() {
         return ShortSpliterators.singleton(this.element, this.comparator);
      }

      @Override
      public ShortSortedSet subSet(short from, short to) {
         return (ShortSortedSet)(this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0 ? this : ShortSortedSets.EMPTY_SET);
      }

      @Override
      public ShortSortedSet headSet(short to) {
         return (ShortSortedSet)(this.compare(this.element, to) < 0 ? this : ShortSortedSets.EMPTY_SET);
      }

      @Override
      public ShortSortedSet tailSet(short from) {
         return (ShortSortedSet)(this.compare(from, this.element) <= 0 ? this : ShortSortedSets.EMPTY_SET);
      }

      @Override
      public short firstShort() {
         return this.element;
      }

      @Override
      public short lastShort() {
         return this.element;
      }

      @Override
      public IntSpliterator intSpliterator() {
         return IntSpliterators.singleton(
            this.element, (left, right) -> this.comparator().compare(SafeMath.safeIntToShort(left), SafeMath.safeIntToShort(right))
         );
      }

      @Deprecated
      @Override
      public ShortSortedSet subSet(Short from, Short to) {
         return this.subSet(from.shortValue(), to.shortValue());
      }

      @Deprecated
      @Override
      public ShortSortedSet headSet(Short to) {
         return this.headSet(to.shortValue());
      }

      @Deprecated
      @Override
      public ShortSortedSet tailSet(Short from) {
         return this.tailSet(from.shortValue());
      }

      @Deprecated
      @Override
      public Short first() {
         return this.element;
      }

      @Deprecated
      @Override
      public Short last() {
         return this.element;
      }
   }

   public static class SynchronizedSortedSet extends ShortSets.SynchronizedSet implements ShortSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortSortedSet sortedSet;

      protected SynchronizedSortedSet(ShortSortedSet s, Object sync) {
         super(s, sync);
         this.sortedSet = s;
      }

      protected SynchronizedSortedSet(ShortSortedSet s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public ShortComparator comparator() {
         synchronized (this.sync) {
            return this.sortedSet.comparator();
         }
      }

      @Override
      public ShortSortedSet subSet(short from, short to) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
      }

      @Override
      public ShortSortedSet headSet(short to) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
      }

      @Override
      public ShortSortedSet tailSet(short from) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
      }

      @Override
      public ShortBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      @Override
      public ShortBidirectionalIterator iterator(short from) {
         return this.sortedSet.iterator(from);
      }

      @Override
      public short firstShort() {
         synchronized (this.sync) {
            return this.sortedSet.firstShort();
         }
      }

      @Override
      public short lastShort() {
         synchronized (this.sync) {
            return this.sortedSet.lastShort();
         }
      }

      @Deprecated
      @Override
      public Short first() {
         synchronized (this.sync) {
            return this.sortedSet.first();
         }
      }

      @Deprecated
      @Override
      public Short last() {
         synchronized (this.sync) {
            return this.sortedSet.last();
         }
      }

      @Deprecated
      @Override
      public ShortSortedSet subSet(Short from, Short to) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
      }

      @Deprecated
      @Override
      public ShortSortedSet headSet(Short to) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
      }

      @Deprecated
      @Override
      public ShortSortedSet tailSet(Short from) {
         return new ShortSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
      }
   }

   public static class UnmodifiableSortedSet extends ShortSets.UnmodifiableSet implements ShortSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortSortedSet sortedSet;

      protected UnmodifiableSortedSet(ShortSortedSet s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public ShortComparator comparator() {
         return this.sortedSet.comparator();
      }

      @Override
      public ShortSortedSet subSet(short from, short to) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
      }

      @Override
      public ShortSortedSet headSet(short to) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(to));
      }

      @Override
      public ShortSortedSet tailSet(short from) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(from));
      }

      @Override
      public ShortBidirectionalIterator iterator() {
         return ShortIterators.unmodifiable(this.sortedSet.iterator());
      }

      @Override
      public ShortBidirectionalIterator iterator(short from) {
         return ShortIterators.unmodifiable(this.sortedSet.iterator(from));
      }

      @Override
      public short firstShort() {
         return this.sortedSet.firstShort();
      }

      @Override
      public short lastShort() {
         return this.sortedSet.lastShort();
      }

      @Deprecated
      @Override
      public Short first() {
         return this.sortedSet.first();
      }

      @Deprecated
      @Override
      public Short last() {
         return this.sortedSet.last();
      }

      @Deprecated
      @Override
      public ShortSortedSet subSet(Short from, Short to) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
      }

      @Deprecated
      @Override
      public ShortSortedSet headSet(Short to) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(to));
      }

      @Deprecated
      @Override
      public ShortSortedSet tailSet(Short from) {
         return new ShortSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(from));
      }
   }
}
