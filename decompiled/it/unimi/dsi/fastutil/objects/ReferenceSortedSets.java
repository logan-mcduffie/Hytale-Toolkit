package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public final class ReferenceSortedSets {
   public static final ReferenceSortedSets.EmptySet EMPTY_SET = new ReferenceSortedSets.EmptySet();

   private ReferenceSortedSets() {
   }

   public static <K> ReferenceSet<K> emptySet() {
      return EMPTY_SET;
   }

   public static <K> ReferenceSortedSet<K> singleton(K element) {
      return new ReferenceSortedSets.Singleton<>(element);
   }

   public static <K> ReferenceSortedSet<K> singleton(K element, Comparator<? super K> comparator) {
      return new ReferenceSortedSets.Singleton<>(element, comparator);
   }

   public static <K> ReferenceSortedSet<K> synchronize(ReferenceSortedSet<K> s) {
      return new ReferenceSortedSets.SynchronizedSortedSet<>(s);
   }

   public static <K> ReferenceSortedSet<K> synchronize(ReferenceSortedSet<K> s, Object sync) {
      return new ReferenceSortedSets.SynchronizedSortedSet<>(s, sync);
   }

   public static <K> ReferenceSortedSet<K> unmodifiable(ReferenceSortedSet<K> s) {
      return new ReferenceSortedSets.UnmodifiableSortedSet<>(s);
   }

   public static class EmptySet<K> extends ReferenceSets.EmptySet<K> implements ReferenceSortedSet<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return ObjectIterators.EMPTY_ITERATOR;
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return ReferenceSortedSets.EMPTY_SET;
      }

      @Override
      public ReferenceSortedSet<K> headSet(K from) {
         return ReferenceSortedSets.EMPTY_SET;
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K to) {
         return ReferenceSortedSets.EMPTY_SET;
      }

      @Override
      public K first() {
         throw new NoSuchElementException();
      }

      @Override
      public K last() {
         throw new NoSuchElementException();
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public Object clone() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return ReferenceSortedSets.EMPTY_SET;
      }
   }

   public static class Singleton<K> extends ReferenceSets.Singleton<K> implements ReferenceSortedSet<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final Comparator<? super K> comparator;

      protected Singleton(K element, Comparator<? super K> comparator) {
         super(element);
         this.comparator = comparator;
      }

      Singleton(K element) {
         this(element, null);
      }

      final int compare(K k1, K k2) {
         return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         ObjectBidirectionalIterator<K> i = this.iterator();
         if (this.compare(this.element, from) <= 0) {
            i.next();
         }

         return i;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.singleton(this.element, this.comparator);
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return (ReferenceSortedSet<K>)(this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0 ? this : ReferenceSortedSets.EMPTY_SET);
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         return (ReferenceSortedSet<K>)(this.compare(this.element, to) < 0 ? this : ReferenceSortedSets.EMPTY_SET);
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         return (ReferenceSortedSet<K>)(this.compare(from, this.element) <= 0 ? this : ReferenceSortedSets.EMPTY_SET);
      }

      @Override
      public K first() {
         return this.element;
      }

      @Override
      public K last() {
         return this.element;
      }
   }

   public static class SynchronizedSortedSet<K> extends ReferenceSets.SynchronizedSet<K> implements ReferenceSortedSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceSortedSet<K> sortedSet;

      protected SynchronizedSortedSet(ReferenceSortedSet<K> s, Object sync) {
         super(s, sync);
         this.sortedSet = s;
      }

      protected SynchronizedSortedSet(ReferenceSortedSet<K> s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public Comparator<? super K> comparator() {
         synchronized (this.sync) {
            return this.sortedSet.comparator();
         }
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return new ReferenceSortedSets.SynchronizedSortedSet<>(this.sortedSet.subSet(from, to), this.sync);
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         return new ReferenceSortedSets.SynchronizedSortedSet<>(this.sortedSet.headSet(to), this.sync);
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         return new ReferenceSortedSets.SynchronizedSortedSet<>(this.sortedSet.tailSet(from), this.sync);
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return this.sortedSet.iterator();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return this.sortedSet.iterator(from);
      }

      @Override
      public K first() {
         synchronized (this.sync) {
            return this.sortedSet.first();
         }
      }

      @Override
      public K last() {
         synchronized (this.sync) {
            return this.sortedSet.last();
         }
      }
   }

   public static class UnmodifiableSortedSet<K> extends ReferenceSets.UnmodifiableSet<K> implements ReferenceSortedSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceSortedSet<K> sortedSet;

      protected UnmodifiableSortedSet(ReferenceSortedSet<K> s) {
         super(s);
         this.sortedSet = s;
      }

      @Override
      public Comparator<? super K> comparator() {
         return this.sortedSet.comparator();
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return new ReferenceSortedSets.UnmodifiableSortedSet<>(this.sortedSet.subSet(from, to));
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         return new ReferenceSortedSets.UnmodifiableSortedSet<>(this.sortedSet.headSet(to));
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         return new ReferenceSortedSets.UnmodifiableSortedSet<>(this.sortedSet.tailSet(from));
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return ObjectIterators.unmodifiable(this.sortedSet.iterator());
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return ObjectIterators.unmodifiable(this.sortedSet.iterator(from));
      }

      @Override
      public K first() {
         return this.sortedSet.first();
      }

      @Override
      public K last() {
         return this.sortedSet.last();
      }
   }
}
