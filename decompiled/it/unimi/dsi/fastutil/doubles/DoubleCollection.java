package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Size64;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface DoubleCollection extends Collection<Double>, DoubleIterable {
   @Override
   DoubleIterator iterator();

   @Override
   default DoubleIterator doubleIterator() {
      return this.iterator();
   }

   @Override
   default DoubleSpliterator spliterator() {
      return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
   }

   @Override
   default DoubleSpliterator doubleSpliterator() {
      return this.spliterator();
   }

   boolean add(double var1);

   boolean contains(double var1);

   boolean rem(double var1);

   @Deprecated
   default boolean add(Double key) {
      return this.add(key.doubleValue());
   }

   @Deprecated
   @Override
   default boolean contains(Object key) {
      return key == null ? false : this.contains(((Double)key).doubleValue());
   }

   @Deprecated
   @Override
   default boolean remove(Object key) {
      return key == null ? false : this.rem((Double)key);
   }

   double[] toDoubleArray();

   @Deprecated
   default double[] toDoubleArray(double[] a) {
      return this.toArray(a);
   }

   double[] toArray(double[] var1);

   boolean addAll(DoubleCollection var1);

   boolean containsAll(DoubleCollection var1);

   boolean removeAll(DoubleCollection var1);

   @Deprecated
   @Override
   default boolean removeIf(Predicate<? super Double> filter) {
      return this.removeIf(filter instanceof java.util.function.DoublePredicate ? (java.util.function.DoublePredicate)filter : key -> filter.test(key));
   }

   default boolean removeIf(java.util.function.DoublePredicate filter) {
      boolean removed = false;
      DoubleIterator each = this.iterator();

      while (each.hasNext()) {
         if (filter.test(each.nextDouble())) {
            each.remove();
            removed = true;
         }
      }

      return removed;
   }

   default boolean removeIf(DoublePredicate filter) {
      return this.removeIf(filter);
   }

   boolean retainAll(DoubleCollection var1);

   @Deprecated
   @Override
   default Stream<Double> stream() {
      return Collection.super.stream();
   }

   default DoubleStream doubleStream() {
      return StreamSupport.doubleStream(this.doubleSpliterator(), false);
   }

   @Deprecated
   @Override
   default Stream<Double> parallelStream() {
      return Collection.super.parallelStream();
   }

   default DoubleStream doubleParallelStream() {
      return StreamSupport.doubleStream(this.doubleSpliterator(), true);
   }
}
