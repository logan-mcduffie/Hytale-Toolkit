package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ReferenceCollections {
   private ReferenceCollections() {
   }

   public static <K> ReferenceCollection<K> synchronize(ReferenceCollection<K> c) {
      return new ReferenceCollections.SynchronizedCollection<>(c);
   }

   public static <K> ReferenceCollection<K> synchronize(ReferenceCollection<K> c, Object sync) {
      return new ReferenceCollections.SynchronizedCollection<>(c, sync);
   }

   public static <K> ReferenceCollection<K> unmodifiable(ReferenceCollection<? extends K> c) {
      return new ReferenceCollections.UnmodifiableCollection<>(c);
   }

   public static <K> ReferenceCollection<K> asCollection(ObjectIterable<K> iterable) {
      return (ReferenceCollection<K>)(iterable instanceof ReferenceCollection
         ? (ReferenceCollection)iterable
         : new ReferenceCollections.IterableCollection<>(iterable));
   }

   public abstract static class EmptyCollection<K> extends AbstractReferenceCollection<K> {
      protected EmptyCollection() {
      }

      @Override
      public boolean contains(Object k) {
         return false;
      }

      @Override
      public Object[] toArray() {
         return ObjectArrays.EMPTY_ARRAY;
      }

      @Override
      public <T> T[] toArray(T[] array) {
         if (array.length > 0) {
            array[0] = null;
         }

         return array;
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return ObjectIterators.EMPTY_ITERATOR;
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.EMPTY_SPLITERATOR;
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public void clear() {
      }

      @Override
      public int hashCode() {
         return 0;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof Collection) ? false : ((Collection)o).isEmpty();
         }
      }

      @Override
      public void forEach(Consumer<? super K> action) {
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
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
         return false;
      }
   }

   public static class IterableCollection<K> extends AbstractReferenceCollection<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ObjectIterable<K> iterable;

      protected IterableCollection(ObjectIterable<K> iterable) {
         this.iterable = Objects.requireNonNull(iterable);
      }

      @Override
      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for (ObjectIterator<K> iterator = this.iterator(); iterator.hasNext(); c++) {
               iterator.next();
            }

            return c;
         }
      }

      @Override
      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      @Override
      public ObjectIterator<K> iterator() {
         return this.iterable.iterator();
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return this.iterable.spliterator();
      }
   }

   static class SizeDecreasingSupplier<K, C extends ReferenceCollection<K>> implements Supplier<C> {
      static final int RECOMMENDED_MIN_SIZE = 8;
      final AtomicInteger suppliedCount = new AtomicInteger(0);
      final int expectedFinalSize;
      final IntFunction<C> builder;

      SizeDecreasingSupplier(int expectedFinalSize, IntFunction<C> builder) {
         this.expectedFinalSize = expectedFinalSize;
         this.builder = builder;
      }

      public C get() {
         int expectedNeededNextSize = 1 + (this.expectedFinalSize - 1) / this.suppliedCount.incrementAndGet();
         if (expectedNeededNextSize < 0) {
            expectedNeededNextSize = 8;
         }

         return this.builder.apply(expectedNeededNextSize);
      }
   }

   static class SynchronizedCollection<K> implements ReferenceCollection<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceCollection<K> collection;
      protected final Object sync;

      protected SynchronizedCollection(ReferenceCollection<K> c, Object sync) {
         this.collection = Objects.requireNonNull(c);
         this.sync = sync;
      }

      protected SynchronizedCollection(ReferenceCollection<K> c) {
         this.collection = Objects.requireNonNull(c);
         this.sync = this;
      }

      @Override
      public boolean add(K k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Override
      public boolean contains(Object k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Override
      public boolean remove(Object k) {
         synchronized (this.sync) {
            return this.collection.remove(k);
         }
      }

      @Override
      public int size() {
         synchronized (this.sync) {
            return this.collection.size();
         }
      }

      @Override
      public boolean isEmpty() {
         synchronized (this.sync) {
            return this.collection.isEmpty();
         }
      }

      @Override
      public Object[] toArray() {
         synchronized (this.sync) {
            return this.collection.toArray();
         }
      }

      @Override
      public <T> T[] toArray(T[] a) {
         synchronized (this.sync) {
            return (T[])this.collection.toArray(a);
         }
      }

      @Override
      public ObjectIterator<K> iterator() {
         return this.collection.iterator();
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return this.collection.spliterator();
      }

      @Override
      public Stream<K> stream() {
         return this.collection.stream();
      }

      @Override
      public Stream<K> parallelStream() {
         return this.collection.parallelStream();
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         synchronized (this.sync) {
            this.collection.forEach(action);
         }
      }

      @Override
      public boolean addAll(Collection<? extends K> c) {
         synchronized (this.sync) {
            return this.collection.addAll(c);
         }
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         synchronized (this.sync) {
            return this.collection.containsAll(c);
         }
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         synchronized (this.sync) {
            return this.collection.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         synchronized (this.sync) {
            return this.collection.retainAll(c);
         }
      }

      @Override
      public boolean removeIf(Predicate<? super K> filter) {
         synchronized (this.sync) {
            return this.collection.removeIf(filter);
         }
      }

      @Override
      public void clear() {
         synchronized (this.sync) {
            this.collection.clear();
         }
      }

      @Override
      public String toString() {
         synchronized (this.sync) {
            return this.collection.toString();
         }
      }

      @Override
      public int hashCode() {
         synchronized (this.sync) {
            return this.collection.hashCode();
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            synchronized (this.sync) {
               return this.collection.equals(o);
            }
         }
      }

      private void writeObject(ObjectOutputStream s) throws IOException {
         synchronized (this.sync) {
            s.defaultWriteObject();
         }
      }
   }

   static class UnmodifiableCollection<K> implements ReferenceCollection<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceCollection<? extends K> collection;

      protected UnmodifiableCollection(ReferenceCollection<? extends K> c) {
         this.collection = Objects.requireNonNull(c);
      }

      @Override
      public boolean add(K k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return this.collection.size();
      }

      @Override
      public boolean isEmpty() {
         return this.collection.isEmpty();
      }

      @Override
      public boolean contains(Object o) {
         return this.collection.contains(o);
      }

      @Override
      public ObjectIterator<K> iterator() {
         return ObjectIterators.unmodifiable(this.collection.iterator());
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return (ObjectSpliterator<K>)this.collection.spliterator();
      }

      @Override
      public Stream<K> stream() {
         return (Stream<K>)this.collection.stream();
      }

      @Override
      public Stream<K> parallelStream() {
         return (Stream<K>)this.collection.parallelStream();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public <T> T[] toArray(T[] a) {
         return (T[])this.collection.toArray(a);
      }

      @Override
      public Object[] toArray() {
         return this.collection.toArray();
      }

      @Override
      public void forEach(Consumer<? super K> action) {
         this.collection.forEach(action);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.collection.containsAll(c);
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
      public String toString() {
         return this.collection.toString();
      }

      @Override
      public int hashCode() {
         return this.collection.hashCode();
      }

      @Override
      public boolean equals(Object o) {
         return o == this ? true : this.collection.equals(o);
      }
   }
}
