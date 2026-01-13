package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public final class FloatCollections {
   private FloatCollections() {
   }

   public static FloatCollection synchronize(FloatCollection c) {
      return new FloatCollections.SynchronizedCollection(c);
   }

   public static FloatCollection synchronize(FloatCollection c, Object sync) {
      return new FloatCollections.SynchronizedCollection(c, sync);
   }

   public static FloatCollection unmodifiable(FloatCollection c) {
      return new FloatCollections.UnmodifiableCollection(c);
   }

   public static FloatCollection asCollection(FloatIterable iterable) {
      return (FloatCollection)(iterable instanceof FloatCollection ? (FloatCollection)iterable : new FloatCollections.IterableCollection(iterable));
   }

   public abstract static class EmptyCollection extends AbstractFloatCollection {
      protected EmptyCollection() {
      }

      @Override
      public boolean contains(float k) {
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

      public FloatBidirectionalIterator iterator() {
         return FloatIterators.EMPTY_ITERATOR;
      }

      @Override
      public FloatSpliterator spliterator() {
         return FloatSpliterators.EMPTY_SPLITERATOR;
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
      public void forEach(Consumer<? super Float> action) {
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends Float> c) {
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
      public boolean removeIf(Predicate<? super Float> filter) {
         return false;
      }

      @Override
      public float[] toFloatArray() {
         return FloatArrays.EMPTY_ARRAY;
      }

      @Deprecated
      @Override
      public float[] toFloatArray(float[] a) {
         return a;
      }

      @Override
      public void forEach(FloatConsumer action) {
      }

      @Override
      public boolean containsAll(FloatCollection c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(FloatPredicate filter) {
         return false;
      }

      @Override
      public DoubleIterator doubleIterator() {
         return DoubleIterators.EMPTY_ITERATOR;
      }

      @Override
      public DoubleSpliterator doubleSpliterator() {
         return DoubleSpliterators.EMPTY_SPLITERATOR;
      }
   }

   public static class IterableCollection extends AbstractFloatCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatIterable iterable;

      protected IterableCollection(FloatIterable iterable) {
         this.iterable = Objects.requireNonNull(iterable);
      }

      @Override
      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for (FloatIterator iterator = this.iterator(); iterator.hasNext(); c++) {
               iterator.nextFloat();
            }

            return c;
         }
      }

      @Override
      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      @Override
      public FloatIterator iterator() {
         return this.iterable.iterator();
      }

      @Override
      public FloatSpliterator spliterator() {
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

   static class SynchronizedCollection implements FloatCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(FloatCollection c, Object sync) {
         this.collection = Objects.requireNonNull(c);
         this.sync = sync;
      }

      protected SynchronizedCollection(FloatCollection c) {
         this.collection = Objects.requireNonNull(c);
         this.sync = this;
      }

      @Override
      public boolean add(float k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Override
      public boolean contains(float k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Override
      public boolean rem(float k) {
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
      public float[] toFloatArray() {
         synchronized (this.sync) {
            return this.collection.toFloatArray();
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
      public float[] toFloatArray(float[] a) {
         return this.toArray(a);
      }

      @Override
      public float[] toArray(float[] a) {
         synchronized (this.sync) {
            return this.collection.toArray(a);
         }
      }

      @Override
      public boolean addAll(FloatCollection c) {
         synchronized (this.sync) {
            return this.collection.addAll(c);
         }
      }

      @Override
      public boolean containsAll(FloatCollection c) {
         synchronized (this.sync) {
            return this.collection.containsAll(c);
         }
      }

      @Override
      public boolean removeAll(FloatCollection c) {
         synchronized (this.sync) {
            return this.collection.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(FloatCollection c) {
         synchronized (this.sync) {
            return this.collection.retainAll(c);
         }
      }

      @Deprecated
      @Override
      public boolean add(Float k) {
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
      public FloatIterator iterator() {
         return this.collection.iterator();
      }

      @Override
      public FloatSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Float> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Float> parallelStream() {
         return this.collection.parallelStream();
      }

      @Override
      public void forEach(FloatConsumer action) {
         synchronized (this.sync) {
            this.collection.forEach(action);
         }
      }

      @Override
      public boolean addAll(Collection<? extends Float> c) {
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
      public boolean removeIf(FloatPredicate filter) {
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

   static class UnmodifiableCollection implements FloatCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatCollection collection;

      protected UnmodifiableCollection(FloatCollection c) {
         this.collection = Objects.requireNonNull(c);
      }

      @Override
      public boolean add(float k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean rem(float k) {
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
      public boolean contains(float o) {
         return this.collection.contains(o);
      }

      @Override
      public FloatIterator iterator() {
         return FloatIterators.unmodifiable(this.collection.iterator());
      }

      @Override
      public FloatSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Deprecated
      @Override
      public Stream<Float> stream() {
         return this.collection.stream();
      }

      @Deprecated
      @Override
      public Stream<Float> parallelStream() {
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
      public void forEach(FloatConsumer action) {
         this.collection.forEach(action);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends Float> c) {
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
      public boolean removeIf(FloatPredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Float k) {
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
      public float[] toFloatArray() {
         return this.collection.toFloatArray();
      }

      @Deprecated
      @Override
      public float[] toFloatArray(float[] a) {
         return this.toArray(a);
      }

      @Override
      public float[] toArray(float[] a) {
         return this.collection.toArray(a);
      }

      @Override
      public boolean containsAll(FloatCollection c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(FloatCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(FloatCollection c) {
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
