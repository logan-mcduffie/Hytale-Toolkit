package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class BooleanCollections {
   private BooleanCollections() {
   }

   public static BooleanCollection synchronize(BooleanCollection c) {
      return new BooleanCollections.SynchronizedCollection(c);
   }

   public static BooleanCollection synchronize(BooleanCollection c, Object sync) {
      return new BooleanCollections.SynchronizedCollection(c, sync);
   }

   public static BooleanCollection unmodifiable(BooleanCollection c) {
      return new BooleanCollections.UnmodifiableCollection(c);
   }

   public static BooleanCollection asCollection(BooleanIterable iterable) {
      return (BooleanCollection)(iterable instanceof BooleanCollection ? (BooleanCollection)iterable : new BooleanCollections.IterableCollection(iterable));
   }

   public abstract static class EmptyCollection extends AbstractBooleanCollection {
      protected EmptyCollection() {
      }

      @Override
      public boolean contains(boolean k) {
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

      public BooleanBidirectionalIterator iterator() {
         return BooleanIterators.EMPTY_ITERATOR;
      }

      @Override
      public BooleanSpliterator spliterator() {
         return BooleanSpliterators.EMPTY_SPLITERATOR;
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
      public void forEach(Consumer<? super Boolean> action) {
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(Collection<? extends Boolean> c) {
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
      public boolean removeIf(Predicate<? super Boolean> filter) {
         return false;
      }

      @Override
      public boolean[] toBooleanArray() {
         return BooleanArrays.EMPTY_ARRAY;
      }

      @Deprecated
      @Override
      public boolean[] toBooleanArray(boolean[] a) {
         return a;
      }

      @Override
      public void forEach(BooleanConsumer action) {
      }

      @Override
      public boolean containsAll(BooleanCollection c) {
         return c.isEmpty();
      }

      @Override
      public boolean addAll(BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(BooleanPredicate filter) {
         return false;
      }
   }

   public static class IterableCollection extends AbstractBooleanCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanIterable iterable;

      protected IterableCollection(BooleanIterable iterable) {
         this.iterable = Objects.requireNonNull(iterable);
      }

      @Override
      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for (BooleanIterator iterator = this.iterator(); iterator.hasNext(); c++) {
               iterator.nextBoolean();
            }

            return c;
         }
      }

      @Override
      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      @Override
      public BooleanIterator iterator() {
         return this.iterable.iterator();
      }

      @Override
      public BooleanSpliterator spliterator() {
         return this.iterable.spliterator();
      }
   }

   static class SynchronizedCollection implements BooleanCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanCollection collection;
      protected final Object sync;

      protected SynchronizedCollection(BooleanCollection c, Object sync) {
         this.collection = Objects.requireNonNull(c);
         this.sync = sync;
      }

      protected SynchronizedCollection(BooleanCollection c) {
         this.collection = Objects.requireNonNull(c);
         this.sync = this;
      }

      @Override
      public boolean add(boolean k) {
         synchronized (this.sync) {
            return this.collection.add(k);
         }
      }

      @Override
      public boolean contains(boolean k) {
         synchronized (this.sync) {
            return this.collection.contains(k);
         }
      }

      @Override
      public boolean rem(boolean k) {
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
      public boolean[] toBooleanArray() {
         synchronized (this.sync) {
            return this.collection.toBooleanArray();
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
      public boolean[] toBooleanArray(boolean[] a) {
         return this.toArray(a);
      }

      @Override
      public boolean[] toArray(boolean[] a) {
         synchronized (this.sync) {
            return this.collection.toArray(a);
         }
      }

      @Override
      public boolean addAll(BooleanCollection c) {
         synchronized (this.sync) {
            return this.collection.addAll(c);
         }
      }

      @Override
      public boolean containsAll(BooleanCollection c) {
         synchronized (this.sync) {
            return this.collection.containsAll(c);
         }
      }

      @Override
      public boolean removeAll(BooleanCollection c) {
         synchronized (this.sync) {
            return this.collection.removeAll(c);
         }
      }

      @Override
      public boolean retainAll(BooleanCollection c) {
         synchronized (this.sync) {
            return this.collection.retainAll(c);
         }
      }

      @Deprecated
      @Override
      public boolean add(Boolean k) {
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
      public <T> T[] toArray(T[] a) {
         synchronized (this.sync) {
            return (T[])this.collection.toArray(a);
         }
      }

      @Override
      public BooleanIterator iterator() {
         return this.collection.iterator();
      }

      @Override
      public BooleanSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Override
      public Stream<Boolean> stream() {
         return this.collection.stream();
      }

      @Override
      public Stream<Boolean> parallelStream() {
         return this.collection.parallelStream();
      }

      @Override
      public void forEach(BooleanConsumer action) {
         synchronized (this.sync) {
            this.collection.forEach(action);
         }
      }

      @Override
      public boolean addAll(Collection<? extends Boolean> c) {
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
      public boolean removeIf(BooleanPredicate filter) {
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

   static class UnmodifiableCollection implements BooleanCollection, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanCollection collection;

      protected UnmodifiableCollection(BooleanCollection c) {
         this.collection = Objects.requireNonNull(c);
      }

      @Override
      public boolean add(boolean k) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean rem(boolean k) {
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
      public boolean contains(boolean o) {
         return this.collection.contains(o);
      }

      @Override
      public BooleanIterator iterator() {
         return BooleanIterators.unmodifiable(this.collection.iterator());
      }

      @Override
      public BooleanSpliterator spliterator() {
         return this.collection.spliterator();
      }

      @Override
      public Stream<Boolean> stream() {
         return this.collection.stream();
      }

      @Override
      public Stream<Boolean> parallelStream() {
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
      public void forEach(BooleanConsumer action) {
         this.collection.forEach(action);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(Collection<? extends Boolean> c) {
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
      public boolean removeIf(BooleanPredicate filter) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public boolean add(Boolean k) {
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
      public boolean[] toBooleanArray() {
         return this.collection.toBooleanArray();
      }

      @Deprecated
      @Override
      public boolean[] toBooleanArray(boolean[] a) {
         return this.toArray(a);
      }

      @Override
      public boolean[] toArray(boolean[] a) {
         return this.collection.toArray(a);
      }

      @Override
      public boolean containsAll(BooleanCollection c) {
         return this.collection.containsAll(c);
      }

      @Override
      public boolean addAll(BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(BooleanCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(BooleanCollection c) {
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
