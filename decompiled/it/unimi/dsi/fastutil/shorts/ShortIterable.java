package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ShortIterable extends Iterable<Short> {
   ShortIterator iterator();

   default IntIterator intIterator() {
      return IntIterators.wrap(this.iterator());
   }

   default ShortSpliterator spliterator() {
      return ShortSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
   }

   default IntSpliterator intSpliterator() {
      return IntSpliterators.wrap(this.spliterator());
   }

   default void forEach(ShortConsumer action) {
      Objects.requireNonNull(action);
      this.iterator().forEachRemaining(action);
   }

   default void forEach(IntConsumer action) {
      this.forEach(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEach(Consumer<? super Short> action) {
      this.forEach(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
   }
}
