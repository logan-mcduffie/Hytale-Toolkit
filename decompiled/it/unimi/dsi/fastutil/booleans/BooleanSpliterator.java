package it.unimi.dsi.fastutil.booleans;

import java.util.Spliterator.OfPrimitive;
import java.util.function.Consumer;

public interface BooleanSpliterator extends OfPrimitive<Boolean, BooleanConsumer, BooleanSpliterator> {
   @Deprecated
   @Override
   default boolean tryAdvance(Consumer<? super Boolean> action) {
      return this.tryAdvance(action instanceof BooleanConsumer ? (BooleanConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Boolean> action) {
      this.forEachRemaining(action instanceof BooleanConsumer ? (BooleanConsumer)action : action::accept);
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

   BooleanSpliterator trySplit();

   default BooleanComparator getComparator() {
      throw new IllegalStateException();
   }
}
