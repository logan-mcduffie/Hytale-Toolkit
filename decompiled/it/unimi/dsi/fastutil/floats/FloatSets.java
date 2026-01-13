package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class FloatSets {
   static final int ARRAY_SET_CUTOFF = 4;
   public static final FloatSets.EmptySet EMPTY_SET = new FloatSets.EmptySet();
   static final FloatSet UNMODIFIABLE_EMPTY_SET = unmodifiable(new FloatArraySet(FloatArrays.EMPTY_ARRAY));

   private FloatSets() {
   }

   public static FloatSet emptySet() {
      return EMPTY_SET;
   }

   public static FloatSet singleton(float element) {
      return new FloatSets.Singleton(element);
   }

   public static FloatSet singleton(Float element) {
      return new FloatSets.Singleton(element);
   }

   public static FloatSet synchronize(FloatSet s) {
      return new FloatSets.SynchronizedSet(s);
   }

   public static FloatSet synchronize(FloatSet s, Object sync) {
      return new FloatSets.SynchronizedSet(s, sync);
   }

   public static FloatSet unmodifiable(FloatSet s) {
      return new FloatSets.UnmodifiableSet(s);
   }

   public static class EmptySet extends FloatCollections.EmptyCollection implements FloatSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      @Override
      public boolean remove(float ok) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object clone() {
         return FloatSets.EMPTY_SET;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof Set && ((Set)o).isEmpty();
      }

      @Deprecated
      @Override
      public boolean rem(float k) {
         return super.rem(k);
      }

      private Object readResolve() {
         return FloatSets.EMPTY_SET;
      }
   }

   public static class Singleton extends AbstractFloatSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final float element;

      protected Singleton(float element) {
         this.element = element;
      }

      @Override
      public boolean contains(float k) {
         return Float.floatToIntBits(k) == Float.floatToIntBits(this.element);
      }

      @Override
      public boolean remove(float k) {
         throw new UnsupportedOperationException();
      }

      public FloatListIterator iterator() {
         return FloatIterators.singleton(this.element);
      }

      @Override
      public FloatSpliterator spliterator() {
         return FloatSpliterators.singleton(this.element);
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public float[] toFloatArray() {
         return new float[]{this.element};
      }

      @Deprecated
      @Override
      public void forEach(Consumer<? super Float> action) {
         action.accept(this.element);
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
         throw new UnsupportedOperationException();
      }

      @Override
      public void forEach(FloatConsumer action) {
         action.accept(this.element);
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
         throw new UnsupportedOperationException();
      }

      @Override
      public DoubleIterator doubleIterator() {
         return DoubleIterators.singleton(this.element);
      }

      @Override
      public DoubleSpliterator doubleSpliterator() {
         return DoubleSpliterators.singleton(this.element);
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

   public static class SynchronizedSet extends FloatCollections.SynchronizedCollection implements FloatSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected SynchronizedSet(FloatSet s, Object sync) {
         super(s, sync);
      }

      protected SynchronizedSet(FloatSet s) {
         super(s);
      }

      @Override
      public boolean remove(float k) {
         synchronized (this.sync) {
            return this.collection.rem(k);
         }
      }

      @Deprecated
      @Override
      public boolean rem(float k) {
         return super.rem(k);
      }
   }

   public static class UnmodifiableSet extends FloatCollections.UnmodifiableCollection implements FloatSet, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected UnmodifiableSet(FloatSet s) {
         super(s);
      }

      @Override
      public boolean remove(float k) {
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
      public boolean rem(float k) {
         return super.rem(k);
      }
   }
}
