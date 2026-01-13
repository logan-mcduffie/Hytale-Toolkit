package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class IntCollections {
   private IntCollections() {
   }

   public static IntCollection synchronize(IntCollection c) {
      return new IntCollections.SynchronizedCollection(c);
   }

   public static IntCollection synchronize(IntCollection c, Object sync) {
      return new IntCollections.SynchronizedCollection(c, sync);
   }

   public static IntCollection unmodifiable(IntCollection c) {
      return new IntCollections.UnmodifiableCollection(c);
   }

   public static IntCollection asCollection(IntIterable iterable) {
      return (IntCollection)(iterable instanceof IntCollection ? (IntCollection)iterable : new IntCollections.IterableCollection(iterable));
   }

   public abstract static class EmptyCollection extends AbstractIntCollection {
      protected EmptyCollection() {
      }

      @Override
      public boolean contains(int k) {
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

      public IntBidirectionalIterator iterator() {
         return IntIterators.EMPTY_ITERATOR;
      }

      @Override
      public IntSpliterator spliterator() {
         return IntSpliterators.EMPTY_SPLITERATOR;
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

      @Deprecated
      @Override
      public void forEach(Consumer<? super Integer> action) {
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends Integer> c) {
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

      @Deprecated
      @Override
      public boolean removeIf(Predicate<? super Integer> filter) {
         return false;
      }

      @Override
      public int[] toIntArray() {
         return IntArrays.EMPTY_ARRAY;
      }

      @Deprecated
      @Override
      public int[] toIntArray(int[] a) {
         return a;
      }

      @Override
      public void forEach(java.util.function.IntConsumer action) {
      }

      @Override
      public boolean containsAll(IntCollection c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(java.util.function.IntPredicate filter) {
         return false;
      }
   }

   public static class IterableCollection extends AbstractIntCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntIterable iterable;

      protected IterableCollection(IntIterable iterable) {
         this.iterable = Objects.requireNonNull(iterable);
      }

      @Override
      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for (IntIterator iterator = this.iterator(); iterator.hasNext(); c++) {
               iterator.nextInt();
            }

            return c;
         }
      }

      @Override
      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      @Override
      public IntIterator iterator() {
         return this.iterable.iterator();
      }

      @Override
      public IntSpliterator spliterator() {
         return this.iterable.spliterator();
      }

      @Override
      public IntIterator intIterator() {
         return this.iterable.intIterator();
      }

      @Override
      public IntSpliterator intSpliterator() {
         return this.iterable.intSpliterator();
      }
   }

   static class SizeDecreasingSupplier<C extends IntCollection> implements Supplier<C> {
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

   static class SynchronizedCollection implements IntCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(IntCollection c, Object sync) {
         this.collection = Objects.requireNonNull(c);
         this.sync = sync;
      }

      protected SynchronizedCollection(IntCollection c) {
         this.collection = Objects.requireNonNull(c);
         this.sync = this;
      }

      @Override
      public boolean add(int k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Override
      public boolean contains(int k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Override
      public boolean rem(int k) {
         synchronized (this.sync) {
            return this.collection.rem(k);
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
      public int[] toIntArray() {
         synchronized (this.sync) {
            return this.collection.toIntArray();
         }
      }

      @Override
      public Object[] toArray() {
         synchronized (this.sync) {
            return this.collection.toArray();
         }
      }

      @Deprecated
      @Override
      public int[] toIntArray(int[] a) {
         return this.toArray(a);
      }

      @Override
      public int[] toArray(int[] a) {
         synchronized (this.sync) {
            return this.collection.toArray(a);
         }
      }

      @Override
      public boolean addAll(IntCollection c) {
         synchronized (this.sync) {
            return this.collection.addAll(c);
         }
      }

      @Override
      public boolean containsAll(IntCollection c) {
         synchronized (this.sync) {
            return this.collection.containsAll(c);
         }
      }

      @Override
      public boolean removeAll(IntCollection c) {
         synchronized (this.sync) {
            return this.collection.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(IntCollection c) {
         synchronized (this.sync) {
            return this.collection.retainAll(c);
         }
      }

      @Deprecated
      @Override
      public boolean add(Integer k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Deprecated
      @Override
      public boolean contains(Object k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Deprecated
      @Override
      public boolean remove(Object k) {
         synchronized (this.sync) {
            return this.collection.remove(k);
         }
      }

      @Override
      public IntIterator intIterator() {
         return this.collection.intIterator();
      }

      @Override
      public IntSpliterator intSpliterator() {
         return this.collection.intSpliterator();
      }

      @Override
      public IntStream intStream() {
         return this.collection.intStream();
      }

      @Override
      public IntStream intParallelStream() {
         return this.collection.intParallelStream();
      }

      @Override
      public <T> T[] toArray(T[] a) {
         synchronized (this.sync) {
            return (T[])this.collection.toArray(a);
         }
      }

      @Override
      public IntIterator iterator() {
         return this.collection.iterator();
      }

      @Override
      public IntSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Integer> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Integer> parallelStream() {
         return this.collection.parallelStream();
      }

      @Override
      public void forEach(java.util.function.IntConsumer action) {
         synchronized (this.sync) {
            this.collection.forEach(action);
         }
      }

      @Override
      public boolean addAll(Collection<? extends Integer> c) {
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
      public boolean removeIf(java.util.function.IntPredicate filter) {
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

   static class UnmodifiableCollection implements IntCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntCollection collection;

      protected UnmodifiableCollection(IntCollection c) {
         this.collection = Objects.requireNonNull(c);
      }

      @Override
      public boolean add(int k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean rem(int k) {
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
      public boolean contains(int o) {
         return this.collection.contains(o);
      }

      @Override
      public IntIterator iterator() {
         return IntIterators.unmodifiable(this.collection.iterator());
      }

      @Override
      public IntSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Integer> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Integer> parallelStream() {
         return this.collection.parallelStream();
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
      public void forEach(java.util.function.IntConsumer action) {
         this.collection.forEach(action);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends Integer> c) {
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
      public boolean removeIf(java.util.function.IntPredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Integer k) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean contains(Object k) {
         return this.collection.contains(k);
      }

      @Deprecated
      @Override
      public boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int[] toIntArray() {
         return this.collection.toIntArray();
      }

      @Deprecated
      @Override
      public int[] toIntArray(int[] a) {
         return this.toArray(a);
      }

      @Override
      public int[] toArray(int[] a) {
         return this.collection.toArray(a);
      }

      @Override
      public boolean containsAll(IntCollection c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public IntIterator intIterator() {
         return this.collection.intIterator();
      }

      @Override
      public IntSpliterator intSpliterator() {
         return this.collection.intSpliterator();
      }

      @Override
      public IntStream intStream() {
         return this.collection.intStream();
      }

      @Override
      public IntStream intParallelStream() {
         return this.collection.intParallelStream();
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
