package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface CharIterable extends Iterable<Character> {
   CharIterator iterator();

   default IntIterator intIterator() {
      return IntIterators.wrap(this.iterator());
   }

   default CharSpliterator spliterator() {
      return CharSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
   }

   default IntSpliterator intSpliterator() {
      return IntSpliterators.wrap(this.spliterator());
   }

   default void forEach(CharConsumer action) {
      Objects.requireNonNull(action);
      this.iterator().forEachRemaining(action);
   }

   default void forEach(IntConsumer action) {
      this.forEach(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEach(Consumer<? super Character> action) {
      this.forEach(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
   }
}
