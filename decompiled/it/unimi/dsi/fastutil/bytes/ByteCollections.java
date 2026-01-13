package it.unimi.dsi.fastutil.bytes;

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

public final class ByteCollections {
   private ByteCollections() {
   }

   public static ByteCollection synchronize(ByteCollection c) {
      return new ByteCollections.SynchronizedCollection(c);
   }

   public static ByteCollection synchronize(ByteCollection c, Object sync) {
      return new ByteCollections.SynchronizedCollection(c, sync);
   }

   public static ByteCollection unmodifiable(ByteCollection c) {
      return new ByteCollections.UnmodifiableCollection(c);
   }

   public static ByteCollection asCollection(ByteIterable iterable) {
      return (ByteCollection)(iterable instanceof ByteCollection ? (ByteCollection)iterable : new ByteCollections.IterableCollection(iterable));
   }

   public abstract static class EmptyCollection extends AbstractByteCollection {
      protected EmptyCollection() {
      }

      @Override
      public boolean contains(byte k) {
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

      public ByteBidirectionalIterator iterator() {
         return ByteIterators.EMPTY_ITERATOR;
      }

      @Override
      public ByteSpliterator spliterator() {
         return ByteSpliterators.EMPTY_SPLITERATOR;
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
      public void forEach(Consumer<? super Byte> action) {
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends Byte> c) {
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
      public boolean removeIf(Predicate<? super Byte> filter) {
         return false;
      }

      @Override
      public byte[] toByteArray() {
         return ByteArrays.EMPTY_ARRAY;
      }

      @Deprecated
      @Override
      public byte[] toByteArray(byte[] a) {
         return a;
      }

      @Override
      public void forEach(ByteConsumer action) {
      }

      @Override
      public boolean containsAll(ByteCollection c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(BytePredicate filter) {
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

   public static class IterableCollection extends AbstractByteCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteIterable iterable;

      protected IterableCollection(ByteIterable iterable) {
         this.iterable = Objects.requireNonNull(iterable);
      }

      @Override
      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for (ByteIterator iterator = this.iterator(); iterator.hasNext(); c++) {
               iterator.nextByte();
            }

            return c;
         }
      }

      @Override
      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      @Override
      public ByteIterator iterator() {
         return this.iterable.iterator();
      }

      @Override
      public ByteSpliterator spliterator() {
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

   static class SynchronizedCollection implements ByteCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(ByteCollection c, Object sync) {
         this.collection = Objects.requireNonNull(c);
         this.sync = sync;
      }

      protected SynchronizedCollection(ByteCollection c) {
         this.collection = Objects.requireNonNull(c);
         this.sync = this;
      }

      @Override
      public boolean add(byte k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Override
      public boolean contains(byte k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Override
      public boolean rem(byte k) {
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
      public byte[] toByteArray() {
         synchronized (this.sync) {
            return this.collection.toByteArray();
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
      public byte[] toByteArray(byte[] a) {
         return this.toArray(a);
      }

      @Override
      public byte[] toArray(byte[] a) {
         synchronized (this.sync) {
            return this.collection.toArray(a);
         }
      }

      @Override
      public boolean addAll(ByteCollection c) {
         synchronized (this.sync) {
            return this.collection.addAll(c);
         }
      }

      @Override
      public boolean containsAll(ByteCollection c) {
         synchronized (this.sync) {
            return this.collection.containsAll(c);
         }
      }

      @Override
      public boolean removeAll(ByteCollection c) {
         synchronized (this.sync) {
            return this.collection.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(ByteCollection c) {
         synchronized (this.sync) {
            return this.collection.retainAll(c);
         }
      }

      @Deprecated
      @Override
      public boolean add(Byte k) {
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
      public ByteIterator iterator() {
         return this.collection.iterator();
      }

      @Override
      public ByteSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Byte> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Byte> parallelStream() {
         return this.collection.parallelStream();
      }

      @Override
      public void forEach(ByteConsumer action) {
         synchronized (this.sync) {
            this.collection.forEach(action);
         }
      }

      @Override
      public boolean addAll(Collection<? extends Byte> c) {
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
      public boolean removeIf(BytePredicate filter) {
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

   static class UnmodifiableCollection implements ByteCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteCollection collection;

      protected UnmodifiableCollection(ByteCollection c) {
         this.collection = Objects.requireNonNull(c);
      }

      @Override
      public boolean add(byte k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean rem(byte k) {
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
      public boolean contains(byte o) {
         return this.collection.contains(o);
      }

      @Override
      public ByteIterator iterator() {
         return ByteIterators.unmodifiable(this.collection.iterator());
      }

      @Override
      public ByteSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Byte> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Byte> parallelStream() {
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
      public void forEach(ByteConsumer action) {
         this.collection.forEach(action);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends Byte> c) {
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
      public boolean removeIf(BytePredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Byte k) {
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
      public byte[] toByteArray() {
         return this.collection.toByteArray();
      }

      @Deprecated
      @Override
      public byte[] toByteArray(byte[] a) {
         return this.toArray(a);
      }

      @Override
      public byte[] toArray(byte[] a) {
         return this.collection.toArray(a);
      }

      @Override
      public boolean containsAll(ByteCollection c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(ByteCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(ByteCollection c) {
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
