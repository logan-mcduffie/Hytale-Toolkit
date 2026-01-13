package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import java.util.Collection;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface FloatCollection extends Collection<Float>, FloatIterable {
   @Override
   FloatIterator iterator();

   @Override
   default DoubleIterator doubleIterator() {
      return FloatIterable.super.doubleIterator();
   }

   @Override
   default FloatSpliterator spliterator() {
      return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
   }

   @Override
   default DoubleSpliterator doubleSpliterator() {
      return FloatIterable.super.doubleSpliterator();
   }

   boolean add(float var1);

   boolean contains(float var1);

   boolean rem(float var1);

   @Deprecated
   default boolean add(Float key) {
      return this.add(key.floatValue());
   }

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return key == null ? false : this.contains(((Float)key).floatValue());
   }

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return key == null ? false : this.rem((Float)key);
   }

   float[] toFloatArray();

   @Deprecated
   default float[] toFloatArray(float[] a) {
      return this.toArray(a);
   }

   float[] toArray(float[] var1);

   boolean addAll(FloatCollection var1);

   boolean containsAll(FloatCollection var1);

   boolean removeAll(FloatCollection var1);

   @Deprecated
   @Override
   default boolean removeIf(Predicate<? super Float> filter) {
      return this.removeIf(filter instanceof FloatPredicate ? (FloatPredicate)filter : key -> filter.test(SafeMath.safeDoubleToFloat(key)));
   }

   default boolean removeIf(FloatPredicate filter) {
      boolean removed = false;
      FloatIterator each = this.iterator();

      while (each.hasNext()) {
         if (filter.test(each.nextFloat())) {
            each.remove();
            removed = true;
         }
      }

      return removed;
   }

   default boolean removeIf(DoublePredicate filter) {
      return this.removeIf(filter instanceof FloatPredicate ? (FloatPredicate)filter : filter::test);
   }

   boolean retainAll(FloatCollection var1);

   @Deprecated
   @Override
   default Stream<Float> stream() {
      return Collection.super.stream();
   }

   default DoubleStream doubleStream() {
      return StreamSupport.doubleStream(this.doubleSpliterator(), false);
   }

   @Deprecated
   @Override
   default Stream<Float> parallelStream() {
      return Collection.super.parallelStream();
   }

   default DoubleStream doubleParallelStream() {
      return StreamSupport.doubleStream(this.doubleSpliterator(), true);
   }
}
