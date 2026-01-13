package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface FloatIterable extends Iterable<Float> {
   FloatIterator iterator();

   default DoubleIterator doubleIterator() {
      return DoubleIterators.wrap(this.iterator());
   }

   default FloatSpliterator spliterator() {
      return FloatSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
   }

   default DoubleSpliterator doubleSpliterator() {
      return DoubleSpliterators.wrap(this.spliterator());
   }

   default void forEach(FloatConsumer action) {
      Objects.requireNonNull(action);
      this.iterator().forEachRemaining(action);
   }

   default void forEach(DoubleConsumer action) {
      this.forEach(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEach(Consumer<? super Float> action) {
      this.forEach(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
   }
}
