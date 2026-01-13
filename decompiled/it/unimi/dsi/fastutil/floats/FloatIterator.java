package it.unimi.dsi.fastutil.floats;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface FloatIterator extends PrimitiveIterator<Float, FloatConsumer> {
   float nextFloat();

   @Deprecated
   default Float next() {
      return this.nextFloat();
   }

   default void forEachRemaining(FloatConsumer action) {
      Objects.requireNonNull(action);

      while (this.hasNext()) {
         action.accept(this.nextFloat());
      }
   }

   default void forEachRemaining(DoubleConsumer action) {
      this.forEachRemaining(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Float> action) {
      this.forEachRemaining(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
   }

   default int skip(int n) {
      if (n < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + n);
      } else {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextFloat();
         }

         return n - i - 1;
      }
   }
}
