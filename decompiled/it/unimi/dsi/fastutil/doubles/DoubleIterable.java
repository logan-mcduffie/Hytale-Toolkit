package it.unimi.dsi.fastutil.doubles;

import java.util.Objects;
import java.util.function.Consumer;

public interface DoubleIterable extends Iterable<Double> {
   DoubleIterator iterator();

   default DoubleIterator doubleIterator() {
      return this.iterator();
   }

   default DoubleSpliterator spliterator() {
      return DoubleSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
   }

   default DoubleSpliterator doubleSpliterator() {
      return this.spliterator();
   }

   default void forEach(java.util.function.DoubleConsumer action) {
      Objects.requireNonNull(action);
      this.iterator().forEachRemaining(action);
   }

   default void forEach(DoubleConsumer action) {
      this.forEach((java.util.function.DoubleConsumer)action);
   }

   @Deprecated
   @Override
   default void forEach(Consumer<? super Double> action) {
      this.forEach(action instanceof java.util.function.DoubleConsumer ? (java.util.function.DoubleConsumer)action : action::accept);
   }
}
