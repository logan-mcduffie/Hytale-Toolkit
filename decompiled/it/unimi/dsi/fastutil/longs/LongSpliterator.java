package it.unimi.dsi.fastutil.longs;

import java.util.Spliterator.OfLong;
import java.util.function.Consumer;

public interface LongSpliterator extends OfLong {
   @Deprecated
   @Override
   default boolean tryAdvance(Consumer<? super Long> action) {
      return this.tryAdvance(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)action : action::accept);
   }

   default boolean tryAdvance(LongConsumer action) {
      return this.tryAdvance((java.util.function.LongConsumer)action);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Long> action) {
      this.forEachRemaining(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)action : action::accept);
   }

   default void forEachRemaining(LongConsumer action) {
      this.forEachRemaining((java.util.function.LongConsumer)action);
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

   LongSpliterator trySplit();

   default LongComparator getComparator() {
      throw new IllegalStateException();
   }
}
