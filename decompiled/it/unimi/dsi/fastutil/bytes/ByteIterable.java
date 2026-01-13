package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ByteIterable extends Iterable<Byte> {
   ByteIterator iterator();

   default IntIterator intIterator() {
      return IntIterators.wrap(this.iterator());
   }

   default ByteSpliterator spliterator() {
      return ByteSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
   }

   default IntSpliterator intSpliterator() {
      return IntSpliterators.wrap(this.spliterator());
   }

   default void forEach(ByteConsumer action) {
      Objects.requireNonNull(action);
      this.iterator().forEachRemaining(action);
   }

   default void forEach(IntConsumer action) {
      this.forEach(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEach(Consumer<? super Byte> action) {
      this.forEach(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
   }
}
