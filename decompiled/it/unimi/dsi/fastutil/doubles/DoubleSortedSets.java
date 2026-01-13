package it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class DoubleSortedSets {
   public static final DoubleSortedSets.EmptySet EMPTY_SET = new DoubleSortedSets.EmptySet();

   private DoubleSortedSets() {
   }

   public static DoubleSortedSet singleton(double element) {
      return new DoubleSortedSets.Singleton(element);
   }

   public static DoubleSortedSet singleton(double element, DoubleComparator comparator) {
      return new DoubleSortedSets.Singleton(element, comparator);
   }

   public static DoubleSortedSet singleton(Object element) {
      return new DoubleSortedSets.Singleton((Double)element);
   }

   public static DoubleSortedSet singleton(Object element, DoubleComparator comparator) {
      return new DoubleSortedSets.Singleton((Double)element, comparator);
   }

   public static DoubleSortedSet synchronize(DoubleSortedSet s) {
      return new DoubleSortedSets.SynchronizedSortedSet(s);
   }

   public static DoubleSortedSet synchronize(DoubleSortedSet s, Object sync) {
      return new DoubleSortedSets.SynchronizedSortedSet(s, sync);
   }

   public static DoubleSortedSet unmodifiable(DoubleSortedSet s) {
      return new DoubleSortedSets.UnmodifiableSortedSet(s);
   }

   public static class EmptySet extends DoubleSets.EmptySet implements DoubleSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return DoubleIterators.EMPTY_ITERATOR;
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet headSet(double from) {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public DoubleSortedSet tailSet(double to) {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Override
      public double firstDouble() {
         throw new NoSuchElementException();
      }

      @Override
      public double lastDouble() {
         throw new NoSuchElementException();
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Deprecated
      @Override
      public DoubleSortedSet subSet(Double from, Double to) {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public DoubleSortedSet headSet(Double from) {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public DoubleSortedSet tailSet(Double to) {
         return DoubleSortedSets.EMPTY_SET;
      }

      @Deprecated
      @Override
      public Double first() {
         throw new NoSuchElementException();
      }

      @Deprecated
      @Override
      public Double last() {
         throw new NoSuchElementException();
      }

      @Override
      public Object clone() {
         return DoubleSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return DoubleSortedSets.EMPTY_SET;
      }
   }

   public static class Singleton extends DoubleSets.Singleton implements DoubleSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final DoubleComparator comparator;

      protected Singleton(double element, DoubleComparator comparator) {
         super(element);
         this.comparator = comparator;
      }

      Singleton(double element) {
         this(element, null);
      }

      final int compare(double k1, double k2) {
         return this.comparator == null ? Double.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         DoubleBidirectionalIterator i = this.iterator();
         if (this.compare(this.element, from) <= 0) {
            i.nextDouble();
         }

         return i;
      }

      @Override
      public DoubleComparator comparator() {
         return this.comparator;
      }

      @Override
      public DoubleSpliterator spliterator() {
         return DoubleSpliterators.singleton(this.element, this.comparator);
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return (DoubleSortedSet)(this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0 ? this : DoubleSortedSets.EMPTY_SET);
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return (DoubleSortedSet)(this.compare(this.element, to) < 0 ? this : DoubleSortedSets.EMPTY_SET);
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return (DoubleSortedSet)(this.compare(from, this.element) <= 0 ? this : DoubleSortedSets.EMPTY_SET);
      }

      @Override
      public double firstDouble() {
         return this.element;
      }

      @Override
      public double lastDouble() {
         return this.element;
      }

      @Deprecated
      @Override
      public DoubleSortedSet subSet(Double from, Double to) {
         return this.subSet(from.doubleValue(), to.doubleValue());
      }

      @Deprecated
      @Override
      public DoubleSortedSet headSet(Double to) {
         return this.headSet(to.doubleValue());
      }

      @Deprecated
      @Override
      public DoubleSortedSet tailSet(Double from) {
         return this.tailSet(from.doubleValue());
      }

      @Deprecated
      @Override
      public Double first() {
         return this.element;
      }

      @Deprecated
      @Override
      public Double last() {
         return this.element;
      }
   }

   public static class SynchronizedSortedSet extends DoubleSets.SynchronizedSet implements DoubleSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleSortedSet sortedSet;

      protected SynchronizedSortedSet(DoubleSortedSet s, Object sync) {
         super(s, sync);
         this.sortedSet = s;
      }

      protected SynchronizedSortedSet(DoubleSortedSet s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public DoubleComparator comparator() {
         synchronized (this.sync) {
            return this.sortedSet.comparator();
         }
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return this.sortedSet.iterator();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return this.sortedSet.iterator(from);
      }

      @Override
      public double firstDouble() {
         synchronized (this.sync) {
            return this.sortedSet.firstDouble();
         }
      }

      @Override
      public double lastDouble() {
         synchronized (this.sync) {
            return this.sortedSet.lastDouble();
         }
      }

      @Deprecated
      @Override
      public Double first() {
         synchronized (this.sync) {
            return this.sortedSet.first();
         }
      }

      @Deprecated
      @Override
      public Double last() {
         synchronized (this.sync) {
            return this.sortedSet.last();
         }
      }

      @Deprecated
      @Override
      public DoubleSortedSet subSet(Double from, Double to) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
      }

      @Deprecated
      @Override
      public DoubleSortedSet headSet(Double to) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
      }

      @Deprecated
      @Override
      public DoubleSortedSet tailSet(Double from) {
         return new DoubleSortedSets.SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
      }
   }

   public static class UnmodifiableSortedSet extends DoubleSets.UnmodifiableSet implements DoubleSortedSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleSortedSet sortedSet;

      protected UnmodifiableSortedSet(DoubleSortedSet s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public DoubleComparator comparator() {
         return this.sortedSet.comparator();
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(to));
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(from));
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return DoubleIterators.unmodifiable(this.sortedSet.iterator());
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return DoubleIterators.unmodifiable(this.sortedSet.iterator(from));
      }

      @Override
      public double firstDouble() {
         return this.sortedSet.firstDouble();
      }

      @Override
      public double lastDouble() {
         return this.sortedSet.lastDouble();
      }

      @Deprecated
      @Override
      public Double first() {
         return this.sortedSet.first();
      }

      @Deprecated
      @Override
      public Double last() {
         return this.sortedSet.last();
      }

      @Deprecated
      @Override
      public DoubleSortedSet subSet(Double from, Double to) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
      }

      @Deprecated
      @Override
      public DoubleSortedSet headSet(Double to) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.headSet(to));
      }

      @Deprecated
      @Override
      public DoubleSortedSet tailSet(Double from) {
         return new DoubleSortedSets.UnmodifiableSortedSet(this.sortedSet.tailSet(from));
      }
   }
}
