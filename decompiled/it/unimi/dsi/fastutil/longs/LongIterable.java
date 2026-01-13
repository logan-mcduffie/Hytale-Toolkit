package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.function.Consumer;

public interface LongIterable extends Iterable<Long> {
   LongIterator iterator();

   default LongIterator longIterator() {
      return this.iterator();
   }

   default LongSpliterator spliterator() {
      return LongSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
   }

   default LongSpliterator longSpliterator() {
      return this.spliterator();
   }

   default void forEach(java.util.function.LongConsumer action) {
      Objects.requireNonNull(action);
      this.iterator().forEachRemaining(action);
   }

   default void forEach(LongConsumer action) {
      this.forEach((java.util.function.LongConsumer)action);
   }

   @Deprecated
   @Override
   default void forEach(Consumer<? super Long> action) {
      this.forEach(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)action : action::accept);
   }
}
