package it.unimi.dsi.fastutil.floats;

import java.util.Spliterator.OfPrimitive;
import java.util.function.Consumer;

public interface FloatSpliterator extends OfPrimitive<Float, FloatConsumer, FloatSpliterator> {
   @Deprecated
   @Override
   default boolean tryAdvance(Consumer<? super Float> action) {
      return this.tryAdvance(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Float> action) {
      this.forEachRemaining(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
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

   FloatSpliterator trySplit();

   default FloatComparator getComparator() {
      throw new IllegalStateException();
   }
}
