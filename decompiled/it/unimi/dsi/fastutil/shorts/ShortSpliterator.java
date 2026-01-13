package it.unimi.dsi.fastutil.shorts;

import java.util.Spliterator.OfPrimitive;
import java.util.function.Consumer;

public interface ShortSpliterator extends OfPrimitive<Short, ShortConsumer, ShortSpliterator> {
   @Deprecated
   @Override
   default boolean tryAdvance(Consumer<? super Short> action) {
      return this.tryAdvance(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Short> action) {
      this.forEachRemaining(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
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

   ShortSpliterator trySplit();

   default ShortComparator getComparator() {
      throw new IllegalStateException();
   }
}
