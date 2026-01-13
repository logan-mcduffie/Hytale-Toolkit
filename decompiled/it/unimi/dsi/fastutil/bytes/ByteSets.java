package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ByteSets {
   static final int ARRAY_SET_CUTOFF = 4;
   public static final ByteSets.EmptySet EMPTY_SET = new ByteSets.EmptySet();
   static final ByteSet UNMODIFIABLE_EMPTY_SET = unmodifiable(new ByteArraySet(ByteArrays.EMPTY_ARRAY));

   private ByteSets() {
   }

   public static ByteSet emptySet() {
      return EMPTY_SET;
   }

   public static ByteSet singleton(byte element) {
      return new ByteSets.Singleton(element);
   }

   public static ByteSet singleton(Byte element) {
      return new ByteSets.Singleton(element);
   }

   public static ByteSet synchronize(ByteSet s) {
      return new ByteSets.SynchronizedSet(s);
   }

   public static ByteSet synchronize(ByteSet s, Object sync) {
      return new ByteSets.SynchronizedSet(s, sync);
   }

   public static ByteSet unmodifiable(ByteSet s) {
      return new ByteSets.UnmodifiableSet(s);
   }

   public static ByteSet fromTo(final byte from, final byte to) {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte x) {
            return x >= from && x < to;
         }

         @Override
         public ByteIterator iterator() {
            return ByteIterators.fromTo(from, to);
         }

         @Override
         public int size() {
            long size = (long)to - from;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static ByteSet from(final byte from) {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte x) {
            return x >= from;
         }

         @Override
         public ByteIterator iterator() {
            return ByteIterators.concat(ByteIterators.fromTo(from, (byte)127), ByteSets.singleton((byte)127).iterator());
         }

         @Override
         public int size() {
            long size = 127L - from + 1L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static ByteSet to(final byte to) {
      return new AbstractByteSet() {
         @Override
         public boolean contains(byte x) {
            return x < to;
         }

         @Override
         public ByteIterator iterator() {
            return ByteIterators.fromTo((byte)-128, to);
         }

         @Override
         public int size() {
            long size = to - -128L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static class EmptySet extends ByteCollections.EmptyCollection implements ByteSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public boolean remove(byte ok) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object clone() {
         return ByteSets.EMPTY_SET;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof Set && ((Set)o).isEmpty();
      }

      @Deprecated
      @Override
      public boolean rem(byte k) {
         return super.rem(k);
      }

      private Object readResolve() {
         return ByteSets.EMPTY_SET;
      }
   }

   public static class Singleton extends AbstractByteSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final byte element;

      protected Singleton(byte element) {
         this.element = element;
      }

      @Override
      public boolean contains(byte k) {
         return k == this.element;
      }

      @Override
      public boolean remove(byte k) {
         throw new UnsupportedOperationException();
      }

      public ByteListIterator iterator() {
         return ByteIterators.singleton(this.element);
      }

      @Override
      public ByteSpliterator spliterator() {
         return ByteSpliterators.singleton(this.element);
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public byte[] toByteArray() {
         return new byte[]{this.element};
      }

      @Deprecated
      @Override
      public void forEach(Consumer<? super Byte> action) {
         action.accept(this.element);
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
         throw new UnsupportedOperationException();
      }

      @Override
      public void forEach(ByteConsumer action) {
         action.accept(this.element);
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

   public static class SynchronizedSet extends ByteCollections.SynchronizedCollection implements ByteSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(ByteSet s, Object sync) {
         super(s, sync);
      }

      protected SynchronizedSet(ByteSet s) {
         super(s);
      }

      @Override
      public boolean remove(byte k) {
         synchronized (this.sync) {
            return this.collection.rem(k);
         }
      }

      @Deprecated
      @Override
      public boolean rem(byte k) {
         return super.rem(k);
      }
   }

   public static class UnmodifiableSet extends ByteCollections.UnmodifiableCollection implements ByteSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(ByteSet s) {
         super(s);
      }

      @Override
      public boolean remove(byte k) {
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
      public boolean rem(byte k) {
         return super.rem(k);
      }
   }
}
