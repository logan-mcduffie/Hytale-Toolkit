package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class ShortCollections {
   private ShortCollections() {
   }

   public static ShortCollection synchronize(ShortCollection c) {
      return new ShortCollections.SynchronizedCollection(c);
   }

   public static ShortCollection synchronize(ShortCollection c, Object sync) {
      return new ShortCollections.SynchronizedCollection(c, sync);
   }

   public static ShortCollection unmodifiable(ShortCollection c) {
      return new ShortCollections.UnmodifiableCollection(c);
   }

   public static ShortCollection asCollection(ShortIterable iterable) {
      return (ShortCollection)(iterable instanceof ShortCollection ? (ShortCollection)iterable : new ShortCollections.IterableCollection(iterable));
   }

   public abstract static class EmptyCollection extends AbstractShortCollection {
      protected EmptyCollection() {
      }

      @Override
      public boolean contains(short k) {
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

      public ShortBidirectionalIterator iterator() {
         return ShortIterators.EMPTY_ITERATOR;
      }

      @Override
      public ShortSpliterator spliterator() {
         return ShortSpliterators.EMPTY_SPLITERATOR;
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
      public void forEach(Consumer<? super Short> action) {
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends Short> c) {
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
      public boolean removeIf(Predicate<? super Short> filter) {
         return false;
      }

      @Override
      public short[] toShortArray() {
         return ShortArrays.EMPTY_ARRAY;
      }

      @Deprecated
      @Override
      public short[] toShortArray(short[] a) {
         return a;
      }

      @Override
      public void forEach(ShortConsumer action) {
      }

      @Override
      public boolean containsAll(ShortCollection c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(ShortPredicate filter) {
         return false;
      }

      @Override
      public IntIterator intIterator() {
         return IntIterators.EMPTY_ITERATOR;
      }

      @Override
      public IntSpliterator intSpliterator() {
         return IntSpliterators.EMPTY_SPLITERATOR;
      }
   }

   public static class IterableCollection extends AbstractShortCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortIterable iterable;

      protected IterableCollection(ShortIterable iterable) {
         this.iterable = Objects.requireNonNull(iterable);
      }

      @Override
      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for (ShortIterator iterator = this.iterator(); iterator.hasNext(); c++) {
               iterator.nextShort();
            }

            return c;
         }
      }

      @Override
      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      @Override
      public ShortIterator iterator() {
         return this.iterable.iterator();
      }

      @Override
      public ShortSpliterator spliterator() {
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

   static class SynchronizedCollection implements ShortCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(ShortCollection c, Object sync) {
         this.collection = Objects.requireNonNull(c);
         this.sync = sync;
      }

      protected SynchronizedCollection(ShortCollection c) {
         this.collection = Objects.requireNonNull(c);
         this.sync = this;
      }

      @Override
      public boolean add(short k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Override
      public boolean contains(short k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Override
      public boolean rem(short k) {
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
      public short[] toShortArray() {
         synchronized (this.sync) {
            return this.collection.toShortArray();
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
      public short[] toShortArray(short[] a) {
         return this.toArray(a);
      }

      @Override
      public short[] toArray(short[] a) {
         synchronized (this.sync) {
            return this.collection.toArray(a);
         }
      }

      @Override
      public boolean addAll(ShortCollection c) {
         synchronized (this.sync) {
            return this.collection.addAll(c);
         }
      }

      @Override
      public boolean containsAll(ShortCollection c) {
         synchronized (this.sync) {
            return this.collection.containsAll(c);
         }
      }

      @Override
      public boolean removeAll(ShortCollection c) {
         synchronized (this.sync) {
            return this.collection.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(ShortCollection c) {
         synchronized (this.sync) {
            return this.collection.retainAll(c);
         }
      }

      @Deprecated
      @Override
      public boolean add(Short k) {
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
      public ShortIterator iterator() {
         return this.collection.iterator();
      }

      @Override
      public ShortSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Short> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Short> parallelStream() {
         return this.collection.parallelStream();
      }

      @Override
      public void forEach(ShortConsumer action) {
         synchronized (this.sync) {
            this.collection.forEach(action);
         }
      }

      @Override
      public boolean addAll(Collection<? extends Short> c) {
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
      public boolean removeIf(ShortPredicate filter) {
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

   static class UnmodifiableCollection implements ShortCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortCollection collection;

      protected UnmodifiableCollection(ShortCollection c) {
         this.collection = Objects.requireNonNull(c);
      }

      @Override
      public boolean add(short k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean rem(short k) {
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
      public boolean contains(short o) {
         return this.collection.contains(o);
      }

      @Override
      public ShortIterator iterator() {
         return ShortIterators.unmodifiable(this.collection.iterator());
      }

      @Override
      public ShortSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Short> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Short> parallelStream() {
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
      public void forEach(ShortConsumer action) {
         this.collection.forEach(action);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends Short> c) {
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
      public boolean removeIf(ShortPredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Short k) {
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
      public short[] toShortArray() {
         return this.collection.toShortArray();
      }

      @Deprecated
      @Override
      public short[] toShortArray(short[] a) {
         return this.toArray(a);
      }

      @Override
      public short[] toArray(short[] a) {
         return this.collection.toArray(a);
      }

      @Override
      public boolean containsAll(ShortCollection c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(ShortCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(ShortCollection c) {
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
