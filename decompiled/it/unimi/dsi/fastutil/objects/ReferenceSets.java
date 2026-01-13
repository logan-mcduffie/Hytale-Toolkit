package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ReferenceSets {
   static final int ARRAY_SET_CUTOFF = 4;
   public static final ReferenceSets.EmptySet EMPTY_SET = new ReferenceSets.EmptySet();
   static final ReferenceSet UNMODIFIABLE_EMPTY_SET = unmodifiable(new ReferenceArraySet(ObjectArrays.EMPTY_ARRAY));

   private ReferenceSets() {
   }

   public static <K> ReferenceSet<K> emptySet() {
      return EMPTY_SET;
   }

   public static <K> ReferenceSet<K> singleton(K element) {
      return new ReferenceSets.Singleton<>(element);
   }

   public static <K> ReferenceSet<K> synchronize(ReferenceSet<K> s) {
      return new ReferenceSets.SynchronizedSet<>(s);
   }

   public static <K> ReferenceSet<K> synchronize(ReferenceSet<K> s, Object sync) {
      return new ReferenceSets.SynchronizedSet<>(s, sync);
   }

   public static <K> ReferenceSet<K> unmodifiable(ReferenceSet<? extends K> s) {
      return new ReferenceSets.UnmodifiableSet<>(s);
   }

   public static class EmptySet<K> extends ReferenceCollections.EmptyCollection<K> implements ReferenceSet<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public boolean remove(Object ok) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object clone() {
         return ReferenceSets.EMPTY_SET;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof Set && ((Set)o).isEmpty();
      }

      private Object readResolve() {
         return ReferenceSets.EMPTY_SET;
      }
   }

   public static class Singleton<K> extends AbstractReferenceSet<K> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K element;

      protected Singleton(K element) {
         this.element = element;
      }

      @Override
      public boolean contains(Object k) {
         return k == this.element;
      }

      @Override
      public boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      public ObjectListIterator<K> iterator() {
         return ObjectIterators.singleton(this.element);
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.singleton(this.element);
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public Object[] toArray() {
         return new Object[]{this.element};
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(Predicate<? super K> filter) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object clone() {
         return this;
      }
   }

   public static class SynchronizedSet<K> extends ReferenceCollections.SynchronizedCollection<K> implements ReferenceSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(ReferenceSet<K> s, Object sync) {
         super(s, sync);
      }

      protected SynchronizedSet(ReferenceSet<K> s) {
         super(s);
      }

      @Override
      public boolean remove(Object k) {
         synchronized (this.sync) {
            return this.collection.remove(k);
         }
      }
   }

   public static class UnmodifiableSet<K> extends ReferenceCollections.UnmodifiableCollection<K> implements ReferenceSet<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(ReferenceSet<? extends K> s) {
         super(s);
      }

      @Override
      public boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.collection.equals(o);
      }

      @Override
      public int hashCode() {
         return this.collection.hashCode();
      }
   }
}
