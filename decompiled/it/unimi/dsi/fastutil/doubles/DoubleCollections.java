package it.unimi.dsi.fastutil.doubles;

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
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public final class DoubleCollections {
   private DoubleCollections() {
   }

   public static DoubleCollection synchronize(DoubleCollection c) {
      return new DoubleCollections.SynchronizedCollection(c);
   }

   public static DoubleCollection synchronize(DoubleCollection c, Object sync) {
      return new DoubleCollections.SynchronizedCollection(c, sync);
   }

   public static DoubleCollection unmodifiable(DoubleCollection c) {
      return new DoubleCollections.UnmodifiableCollection(c);
   }

   public static DoubleCollection asCollection(DoubleIterable iterable) {
      return (DoubleCollection)(iterable instanceof DoubleCollection ? (DoubleCollection)iterable : new DoubleCollections.IterableCollection(iterable));
   }

   public abstract static class EmptyCollection extends AbstractDoubleCollection {
      protected EmptyCollection() {
      }

      @Override
      public boolean contains(double k) {
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

      public DoubleBidirectionalIterator iterator() {
         return DoubleIterators.EMPTY_ITERATOR;
      }

      @Override
      public DoubleSpliterator spliterator() {
         return DoubleSpliterators.EMPTY_SPLITERATOR;
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
      public void forEach(Consumer<? super Double> action) {
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends Double> c) {
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
      public boolean removeIf(Predicate<? super Double> filter) {
         return false;
      }

      @Override
      public double[] toDoubleArray() {
         return DoubleArrays.EMPTY_ARRAY;
      }

      @Deprecated
      @Override
      public double[] toDoubleArray(double[] a) {
         return a;
      }

      @Override
      public void forEach(java.util.function.DoubleConsumer action) {
      }

      @Override
      public boolean containsAll(DoubleCollection c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(DoubleCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(DoubleCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(DoubleCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(java.util.function.DoublePredicate filter) {
         return false;
      }
   }

   public static class IterableCollection extends AbstractDoubleCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleIterable iterable;

      protected IterableCollection(DoubleIterable iterable) {
         this.iterable = Objects.requireNonNull(iterable);
      }

      @Override
      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for (DoubleIterator iterator = this.iterator(); iterator.hasNext(); c++) {
               iterator.nextDouble();
            }

            return c;
         }
      }

      @Override
      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      @Override
      public DoubleIterator iterator() {
         return this.iterable.iterator();
      }

      @Override
      public DoubleSpliterator spliterator() {
         return this.iterable.spliterator();
      }

      @Override
      public DoubleIterator doubleIterator() {
         return this.iterable.doubleIterator();
      }

      @Override
      public DoubleSpliterator doubleSpliterator() {
         return this.iterable.doubleSpliterator();
      }
   }

   static class SizeDecreasingSupplier<C extends DoubleCollection> implements Supplier<C> {
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

   static class SynchronizedCollection implements DoubleCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(DoubleCollection c, Object sync) {
         this.collection = Objects.requireNonNull(c);
         this.sync = sync;
      }

      protected SynchronizedCollection(DoubleCollection c) {
         this.collection = Objects.requireNonNull(c);
         this.sync = this;
      }

      @Override
      public boolean add(double k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Override
      public boolean contains(double k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Override
      public boolean rem(double k) {
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
      public double[] toDoubleArray() {
         synchronized (this.sync) {
            return this.collection.toDoubleArray();
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
      public double[] toDoubleArray(double[] a) {
         return this.toArray(a);
      }

      @Override
      public double[] toArray(double[] a) {
         synchronized (this.sync) {
            return this.collection.toArray(a);
         }
      }

      @Override
      public boolean addAll(DoubleCollection c) {
         synchronized (this.sync) {
            return this.collection.addAll(c);
         }
      }

      @Override
      public boolean containsAll(DoubleCollection c) {
         synchronized (this.sync) {
            return this.collection.containsAll(c);
         }
      }

      @Override
      public boolean removeAll(DoubleCollection c) {
         synchronized (this.sync) {
            return this.collection.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(DoubleCollection c) {
         synchronized (this.sync) {
            return this.collection.retainAll(c);
         }
      }

      @Deprecated
      @Override
      public boolean add(Double k) {
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
      public DoubleIterator doubleIterator() {
         return this.collection.doubleIterator();
      }

      @Override
      public DoubleSpliterator doubleSpliterator() {
         return this.collection.doubleSpliterator();
      }

      @Override
      public DoubleStream doubleStream() {
         return this.collection.doubleStream();
      }

      @Override
      public DoubleStream doubleParallelStream() {
         return this.collection.doubleParallelStream();
      }

      @Override
      public <T> T[] toArray(T[] a) {
         synchronized (this.sync) {
            return (T[])this.collection.toArray(a);
         }
      }

      @Override
      public DoubleIterator iterator() {
         return this.collection.iterator();
      }

      @Override
      public DoubleSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Double> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Double> parallelStream() {
         return this.collection.parallelStream();
      }

      @Override
      public void forEach(java.util.function.DoubleConsumer action) {
         synchronized (this.sync) {
            this.collection.forEach(action);
         }
      }

      @Override
      public boolean addAll(Collection<? extends Double> c) {
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
      public boolean removeIf(java.util.function.DoublePredicate filter) {
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

   static class UnmodifiableCollection implements DoubleCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleCollection collection;

      protected UnmodifiableCollection(DoubleCollection c) {
         this.collection = Objects.requireNonNull(c);
      }

      @Override
      public boolean add(double k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean rem(double k) {
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
      public boolean contains(double o) {
         return this.collection.contains(o);
      }

      @Override
      public DoubleIterator iterator() {
         return DoubleIterators.unmodifiable(this.collection.iterator());
      }

      @Override
      public DoubleSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Double> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Double> parallelStream() {
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
      public void forEach(java.util.function.DoubleConsumer action) {
         this.collection.forEach(action);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends Double> c) {
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
      public boolean removeIf(java.util.function.DoublePredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Double k) {
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
      public double[] toDoubleArray() {
         return this.collection.toDoubleArray();
      }

      @Deprecated
      @Override
      public double[] toDoubleArray(double[] a) {
         return this.toArray(a);
      }

      @Override
      public double[] toArray(double[] a) {
         return this.collection.toArray(a);
      }

      @Override
      public boolean containsAll(DoubleCollection c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(DoubleCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(DoubleCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(DoubleCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public DoubleIterator doubleIterator() {
         return this.collection.doubleIterator();
      }

      @Override
      public DoubleSpliterator doubleSpliterator() {
         return this.collection.doubleSpliterator();
      }

      @Override
      public DoubleStream doubleStream() {
         return this.collection.doubleStream();
      }

      @Override
      public DoubleStream doubleParallelStream() {
         return this.collection.doubleParallelStream();
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
