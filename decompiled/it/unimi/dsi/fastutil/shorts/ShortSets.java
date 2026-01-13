package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ShortSets {
   static final int ARRAY_SET_CUTOFF = 4;
   public static final ShortSets.EmptySet EMPTY_SET = new ShortSets.EmptySet();
   static final ShortSet UNMODIFIABLE_EMPTY_SET = unmodifiable(new ShortArraySet(ShortArrays.EMPTY_ARRAY));

   private ShortSets() {
   }

   public static ShortSet emptySet() {
      return EMPTY_SET;
   }

   public static ShortSet singleton(short element) {
      return new ShortSets.Singleton(element);
   }

   public static ShortSet singleton(Short element) {
      return new ShortSets.Singleton(element);
   }

   public static ShortSet synchronize(ShortSet s) {
      return new ShortSets.SynchronizedSet(s);
   }

   public static ShortSet synchronize(ShortSet s, Object sync) {
      return new ShortSets.SynchronizedSet(s, sync);
   }

   public static ShortSet unmodifiable(ShortSet s) {
      return new ShortSets.UnmodifiableSet(s);
   }

   public static ShortSet fromTo(final short from, final short to) {
      return new AbstractShortSet() {
         @Override
         public boolean contains(short x) {
            return x >= from && x < to;
         }

         @Override
         public ShortIterator iterator() {
            return ShortIterators.fromTo(from, to);
         }

         @Override
         public int size() {
            long size = (long)to - from;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static ShortSet from(final short from) {
      return new AbstractShortSet() {
         @Override
         public boolean contains(short x) {
            return x >= from;
         }

         @Override
         public ShortIterator iterator() {
            return ShortIterators.concat(ShortIterators.fromTo(from, (short)32767), ShortSets.singleton((short)32767).iterator());
         }

         @Override
         public int size() {
            long size = 32767L - from + 1L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static ShortSet to(final short to) {
      return new AbstractShortSet() {
         @Override
         public boolean contains(short x) {
            return x < to;
         }

         @Override
         public ShortIterator iterator() {
            return ShortIterators.fromTo((short)-32768, to);
         }

         @Override
         public int size() {
            long size = to - -32768L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static class EmptySet extends ShortCollections.EmptyCollection implements ShortSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public boolean remove(short ok) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object clone() {
         return ShortSets.EMPTY_SET;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof Set && ((Set)o).isEmpty();
      }

      @Deprecated
      @Override
      public boolean rem(short k) {
         return super.rem(k);
      }

      private Object readResolve() {
         return ShortSets.EMPTY_SET;
      }
   }

   public static class Singleton extends AbstractShortSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final short element;

      protected Singleton(short element) {
         this.element = element;
      }

      @Override
      public boolean contains(short k) {
         return k == this.element;
      }

      @Override
      public boolean remove(short k) {
         throw new UnsupportedOperationException();
      }

      public ShortListIterator iterator() {
         return ShortIterators.singleton(this.element);
      }

      @Override
      public ShortSpliterator spliterator() {
         return ShortSpliterators.singleton(this.element);
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public short[] toShortArray() {
         return new short[]{this.element};
      }

      @Deprecated
      @Override
      public void forEach(Consumer<? super Short> action) {
         action.accept(this.element);
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
         throw new UnsupportedOperationException();
      }

      @Override
      public void forEach(ShortConsumer action) {
         action.accept(this.element);
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
         throw new UnsupportedOperationException();
      }

      @Override
      public IntIterator intIterator() {
         return IntIterators.singleton(this.element);
      }

      @Override
      public IntSpliterator intSpliterator() {
         return IntSpliterators.singleton(this.element);
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

   public static class SynchronizedSet extends ShortCollections.SynchronizedCollection implements ShortSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(ShortSet s, Object sync) {
         super(s, sync);
      }

      protected SynchronizedSet(ShortSet s) {
         super(s);
      }

      @Override
      public boolean remove(short k) {
         synchronized (this.sync) {
            return this.collection.rem(k);
         }
      }

      @Deprecated
      @Override
      public boolean rem(short k) {
         return super.rem(k);
      }
   }

   public static class UnmodifiableSet extends ShortCollections.UnmodifiableCollection implements ShortSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(ShortSet s) {
         super(s);
      }

      @Override
      public boolean remove(short k) {
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
      public boolean rem(short k) {
         return super.rem(k);
      }
   }
}
