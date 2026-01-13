package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CharSets {
   static final int ARRAY_SET_CUTOFF = 4;
   public static final CharSets.EmptySet EMPTY_SET = new CharSets.EmptySet();
   static final CharSet UNMODIFIABLE_EMPTY_SET = unmodifiable(new CharArraySet(CharArrays.EMPTY_ARRAY));

   private CharSets() {
   }

   public static CharSet emptySet() {
      return EMPTY_SET;
   }

   public static CharSet singleton(char element) {
      return new CharSets.Singleton(element);
   }

   public static CharSet singleton(Character element) {
      return new CharSets.Singleton(element);
   }

   public static CharSet synchronize(CharSet s) {
      return new CharSets.SynchronizedSet(s);
   }

   public static CharSet synchronize(CharSet s, Object sync) {
      return new CharSets.SynchronizedSet(s, sync);
   }

   public static CharSet unmodifiable(CharSet s) {
      return new CharSets.UnmodifiableSet(s);
   }

   public static CharSet fromTo(final char from, final char to) {
      return new AbstractCharSet() {
         @Override
         public boolean contains(char x) {
            return x >= from && x < to;
         }

         @Override
         public CharIterator iterator() {
            return CharIterators.fromTo(from, to);
         }

         @Override
         public int size() {
            long size = (long)to - from;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static CharSet from(final char from) {
      return new AbstractCharSet() {
         @Override
         public boolean contains(char x) {
            return x >= from;
         }

         @Override
         public CharIterator iterator() {
            return CharIterators.concat(CharIterators.fromTo(from, '\uffff'), CharSets.singleton('\uffff').iterator());
         }

         @Override
         public int size() {
            long size = 65535L - from + 1L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static CharSet to(final char to) {
      return new AbstractCharSet() {
         @Override
         public boolean contains(char x) {
            return x < to;
         }

         @Override
         public CharIterator iterator() {
            return CharIterators.fromTo('\u0000', to);
         }

         @Override
         public int size() {
            long size = to - 0L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static class EmptySet extends CharCollections.EmptyCollection implements CharSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public boolean remove(char ok) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object clone() {
         return CharSets.EMPTY_SET;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof Set && ((Set)o).isEmpty();
      }

      @Deprecated
      @Override
      public boolean rem(char k) {
         return super.rem(k);
      }

      private Object readResolve() {
         return CharSets.EMPTY_SET;
      }
   }

   public static class Singleton extends AbstractCharSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final char element;

      protected Singleton(char element) {
         this.element = element;
      }

      @Override
      public boolean contains(char k) {
         return k == this.element;
      }

      @Override
      public boolean remove(char k) {
         throw new UnsupportedOperationException();
      }

      public CharListIterator iterator() {
         return CharIterators.singleton(this.element);
      }

      @Override
      public CharSpliterator spliterator() {
         return CharSpliterators.singleton(this.element);
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public char[] toCharArray() {
         return new char[]{this.element};
      }

      @Deprecated
      @Override
      public void forEach(Consumer<? super Character> action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(Collection<? extends Character> c) {
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
      public boolean removeIf(Predicate<? super Character> filter) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void forEach(CharConsumer action) {
         action.accept(this.element);
      }

      @Override
      public boolean addAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(CharCollection c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeIf(CharPredicate filter) {
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

   public static class SynchronizedSet extends CharCollections.SynchronizedCollection implements CharSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(CharSet s, Object sync) {
         super(s, sync);
      }

      protected SynchronizedSet(CharSet s) {
         super(s);
      }

      @Override
      public boolean remove(char k) {
         synchronized (this.sync) {
            return this.collection.rem(k);
         }
      }

      @Deprecated
      @Override
      public boolean rem(char k) {
         return super.rem(k);
      }
   }

   public static class UnmodifiableSet extends CharCollections.UnmodifiableCollection implements CharSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(CharSet s) {
         super(s);
      }

      @Override
      public boolean remove(char k) {
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
      public boolean rem(char k) {
         return super.rem(k);
      }
   }
}
