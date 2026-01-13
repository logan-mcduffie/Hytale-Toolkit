package it.unimi.dsi.fastutil.booleans;

import java.util.Objects;
import java.util.function.Consumer;

public interface BooleanIterable extends Iterable<Boolean> {
   BooleanIterator iterator();

   default BooleanSpliterator spliterator() {
      return BooleanSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
   }

   default void forEach(BooleanConsumer action) {
      Objects.requireNonNull(action);
      this.iterator().forEachRemaining(action);
   }

   @Deprecated
   @Override
   default void forEach(Consumer<? super Boolean> action) {
      this.forEach(action instanceof BooleanConsumer ? (BooleanConsumer)action : action::accept);
   }
}
