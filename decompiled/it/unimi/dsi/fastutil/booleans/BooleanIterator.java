package it.unimi.dsi.fastutil.booleans;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;

public interface BooleanIterator extends PrimitiveIterator<Boolean, BooleanConsumer> {
   boolean nextBoolean();

   @Deprecated
   default Boolean next() {
      return this.nextBoolean();
   }

   default void forEachRemaining(BooleanConsumer action) {
      Objects.requireNonNull(action);

      while (this.hasNext()) {
         action.accept(this.nextBoolean());
      }
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Boolean> action) {
      this.forEachRemaining(action instanceof BooleanConsumer ? (BooleanConsumer)action : action::accept);
   }

   default int skip(int n) {
      if (n < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + n);
      } else {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextBoolean();
         }

         return n - i - 1;
      }
   }
}
