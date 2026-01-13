package it.unimi.dsi.fastutil.longs;

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
import java.util.stream.LongStream;
import java.util.stream.Stream;

public final class LongCollections {
   private LongCollections() {
   }

   public static LongCollection synchronize(LongCollection c) {
      return new LongCollections.SynchronizedCollection(c);
   }

   public static LongCollection synchronize(LongCollection c, Object sync) {
      return new LongCollections.SynchronizedCollection(c, sync);
   }

   public static LongCollection unmodifiable(LongCollection c) {
      return new LongCollections.UnmodifiableCollection(c);
   }

   public static LongCollection asCollection(LongIterable iterable) {
      return (LongCollection)(iterable instanceof LongCollection ? (LongCollection)iterable : new LongCollections.IterableCollection(iterable));
   }

   public abstract static class EmptyCollection extends AbstractLongCollection {
      protected EmptyCollection() {
      }

      @Override
      public boolean contains(long k) {
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

      public LongBidirectionalIterator iterator() {
         return LongIterators.EMPTY_ITERATOR;
      }

      @Override
      public LongSpliterator spliterator() {
         return LongSpliterators.EMPTY_SPLITERATOR;
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
      public void forEach(Consumer<? super Long> action) {
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends Long> c) {
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
      public boolean removeIf(Predicate<? super Long> filter) {
         return false;
      }

      @Override
      public long[] toLongArray() {
         return LongArrays.EMPTY_ARRAY;
      }

      @Deprecated
      @Override
      public long[] toLongArray(long[] a) {
         return a;
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
      }

      @Override
      public boolean containsAll(LongCollection c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(java.util.function.LongPredicate filter) {
         return false;
      }
   }

   public static class IterableCollection extends AbstractLongCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongIterable iterable;

      protected IterableCollection(LongIterable iterable) {
         this.iterable = Objects.requireNonNull(iterable);
      }

      @Override
      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for (LongIterator iterator = this.iterator(); iterator.hasNext(); c++) {
               iterator.nextLong();
            }

            return c;
         }
      }

      @Override
      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      @Override
      public LongIterator iterator() {
         return this.iterable.iterator();
      }

      @Override
      public LongSpliterator spliterator() {
         return this.iterable.spliterator();
      }

      @Override
      public LongIterator longIterator() {
         return this.iterable.longIterator();
      }

      @Override
      public LongSpliterator longSpliterator() {
         return this.iterable.longSpliterator();
      }
   }

   static class SizeDecreasingSupplier<C extends LongCollection> implements Supplier<C> {
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

   static class SynchronizedCollection implements LongCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(LongCollection c, Object sync) {
         this.collection = Objects.requireNonNull(c);
         this.sync = sync;
      }

      protected SynchronizedCollection(LongCollection c) {
         this.collection = Objects.requireNonNull(c);
         this.sync = this;
      }

      @Override
      public boolean add(long k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Override
      public boolean contains(long k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Override
      public boolean rem(long k) {
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
      public long[] toLongArray() {
         synchronized (this.sync) {
            return this.collection.toLongArray();
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
      public long[] toLongArray(long[] a) {
         return this.toArray(a);
      }

      @Override
      public long[] toArray(long[] a) {
         synchronized (this.sync) {
            return this.collection.toArray(a);
         }
      }

      @Override
      public boolean addAll(LongCollection c) {
         synchronized (this.sync) {
            return this.collection.addAll(c);
         }
      }

      @Override
      public boolean containsAll(LongCollection c) {
         synchronized (this.sync) {
            return this.collection.containsAll(c);
         }
      }

      @Override
      public boolean removeAll(LongCollection c) {
         synchronized (this.sync) {
            return this.collection.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(LongCollection c) {
         synchronized (this.sync) {
            return this.collection.retainAll(c);
         }
      }

      @Deprecated
      @Override
      public boolean add(Long k) {
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
      public LongIterator longIterator() {
         return this.collection.longIterator();
      }

      @Override
      public LongSpliterator longSpliterator() {
         return this.collection.longSpliterator();
      }

      @Override
      public LongStream longStream() {
         return this.collection.longStream();
      }

      @Override
      public LongStream longParallelStream() {
         return this.collection.longParallelStream();
      }

      @Override
      public <T> T[] toArray(T[] a) {
         synchronized (this.sync) {
            return (T[])this.collection.toArray(a);
         }
      }

      @Override
      public LongIterator iterator() {
         return this.collection.iterator();
      }

      @Override
      public LongSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Long> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Long> parallelStream() {
         return this.collection.parallelStream();
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
         synchronized (this.sync) {
            this.collection.forEach(action);
         }
      }

      @Override
      public boolean addAll(Collection<? extends Long> c) {
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
      public boolean removeIf(java.util.function.LongPredicate filter) {
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

   static class UnmodifiableCollection implements LongCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongCollection collection;

      protected UnmodifiableCollection(LongCollection c) {
         this.collection = Objects.requireNonNull(c);
      }

      @Override
      public boolean add(long k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean rem(long k) {
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
      public boolean contains(long o) {
         return this.collection.contains(o);
      }

      @Override
      public LongIterator iterator() {
         return LongIterators.unmodifiable(this.collection.iterator());
      }

      @Override
      public LongSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Long> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Long> parallelStream() {
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
      public void forEach(java.util.function.LongConsumer action) {
         this.collection.forEach(action);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends Long> c) {
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
      public boolean removeIf(java.util.function.LongPredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Long k) {
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
      public long[] toLongArray() {
         return this.collection.toLongArray();
      }

      @Deprecated
      @Override
      public long[] toLongArray(long[] a) {
         return this.toArray(a);
      }

      @Override
      public long[] toArray(long[] a) {
         return this.collection.toArray(a);
      }

      @Override
      public boolean containsAll(LongCollection c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(LongCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public LongIterator longIterator() {
         return this.collection.longIterator();
      }

      @Override
      public LongSpliterator longSpliterator() {
         return this.collection.longSpliterator();
      }

      @Override
      public LongStream longStream() {
         return this.collection.longStream();
      }

      @Override
      public LongStream longParallelStream() {
         return this.collection.longParallelStream();
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
