package it.unimi.dsi.fastutil.longs;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class LongSets {
   static final int ARRAY_SET_CUTOFF = 4;
   public static final LongSets.EmptySet EMPTY_SET = new LongSets.EmptySet();
   static final LongSet UNMODIFIABLE_EMPTY_SET = unmodifiable(new LongArraySet(LongArrays.EMPTY_ARRAY));

   private LongSets() {
   }

   public static LongSet emptySet() {
      return EMPTY_SET;
   }

   public static LongSet singleton(long element) {
      return new LongSets.Singleton(element);
   }

   public static LongSet singleton(Long element) {
      return new LongSets.Singleton(element);
   }

   public static LongSet synchronize(LongSet s) {
      return new LongSets.SynchronizedSet(s);
   }

   public static LongSet synchronize(LongSet s, Object sync) {
      return new LongSets.SynchronizedSet(s, sync);
   }

   public static LongSet unmodifiable(LongSet s) {
      return new LongSets.UnmodifiableSet(s);
   }

   public static LongSet fromTo(final long from, final long to) {
      return new AbstractLongSet() {
         @Override
         public boolean contains(long x) {
            return x >= from && x < to;
         }

         @Override
         public LongIterator iterator() {
            return LongIterators.fromTo(from, to);
         }

         @Override
         public int size() {
            long size = to - from;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static LongSet from(final long from) {
      return new AbstractLongSet() {
         @Override
         public boolean contains(long x) {
            return x >= from;
         }

         @Override
         public LongIterator iterator() {
            return LongIterators.concat(LongIterators.fromTo(from, Long.MAX_VALUE), LongSets.singleton(Long.MAX_VALUE).iterator());
         }

         @Override
         public int size() {
            long size = Long.MAX_VALUE - from + 1L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static LongSet to(final long to) {
      return new AbstractLongSet() {
         @Override
         public boolean contains(long x) {
            return x < to;
         }

         @Override
         public LongIterator iterator() {
            return LongIterators.fromTo(Long.MIN_VALUE, to);
         }

         @Override
         public int size() {
            long size = to - Long.MIN_VALUE;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static class EmptySet extends LongCollections.EmptyCollection implements LongSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public boolean remove(long ok) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object clone() {
         return LongSets.EMPTY_SET;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof Set && ((Set)o).isEmpty();
      }

      @Deprecated
      @Override
      public boolean rem(long k) {
         return super.rem(k);
      }

      private Object readResolve() {
         return LongSets.EMPTY_SET;
      }
   }

   public static class Singleton extends AbstractLongSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final long element;

      protected Singleton(long element) {
         this.element = element;
      }

      @Override
      public boolean contains(long k) {
         return k == this.element;
      }

      @Override
      public boolean remove(long k) {
         throw new UnsupportedOperationException();
      }

      public LongListIterator iterator() {
         return LongIterators.singleton(this.element);
      }

      @Override
      public LongSpliterator spliterator() {
         return LongSpliterators.singleton(this.element);
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public long[] toLongArray() {
         return new long[]{this.element};
      }

      @Deprecated
      @Override
      public void forEach(Consumer<? super Long> action) {
         action.accept(this.element);
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
         throw new UnsupportedOperationException();
      }

      @Override
      public void forEach(java.util.function.LongConsumer action) {
         action.accept(this.element);
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
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @Override
      public Object[] toArray() {
         return new Object[]{this.element};
      }

      @Override
      public Object clone() {
         return this;
      }
   }

   public static class SynchronizedSet extends LongCollections.SynchronizedCollection implements LongSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(LongSet s, Object sync) {
         super(s, sync);
      }

      protected SynchronizedSet(LongSet s) {
         super(s);
      }

      @Override
      public boolean remove(long k) {
         synchronized (this.sync) {
            return this.collection.rem(k);
         }
      }

      @Deprecated
      @Override
      public boolean rem(long k) {
         return super.rem(k);
      }
   }

   public static class UnmodifiableSet extends LongCollections.UnmodifiableCollection implements LongSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(LongSet s) {
         super(s);
      }

      @Override
      public boolean remove(long k) {
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

      @Deprecated
      @Override
      public boolean rem(long k) {
         return super.rem(k);
      }
   }
}
