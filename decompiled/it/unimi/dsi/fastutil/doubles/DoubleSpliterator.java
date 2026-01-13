package it.unimi.dsi.fastutil.doubles;

import java.util.Spliterator.OfDouble;
import java.util.function.Consumer;

public interface DoubleSpliterator extends OfDouble {
   @Deprecated
   @Override
   default boolean tryAdvance(Consumer<? super Double> action) {
      return this.tryAdvance(action instanceof java.util.function.DoubleConsumer ? (java.util.function.DoubleConsumer)action : action::accept);
   }

   default boolean tryAdvance(DoubleConsumer action) {
      return this.tryAdvance((java.util.function.DoubleConsumer)action);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Double> action) {
      this.forEachRemaining(action instanceof java.util.function.DoubleConsumer ? (java.util.function.DoubleConsumer)action : action::accept);
   }

   default void forEachRemaining(DoubleConsumer action) {
      this.forEachRemaining((java.util.function.DoubleConsumer)action);
   }

   default long skip(long n) {
      if (n < 0L) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + n);
      } else {
         long i = n;

         while (i-- != 0L && this.tryAdvance(unused -> {})) {
         }

         return n - i - 1L;
      }
   }

   DoubleSpliterator trySplit();

   default DoubleComparator getComparator() {
      throw new IllegalStateException();
   }
}
