package it.unimi.dsi.fastutil.doubles;

import java.util.PrimitiveIterator.OfDouble;
import java.util.function.Consumer;

public interface DoubleIterator extends OfDouble {
   @Override
   double nextDouble();

   @Deprecated
   @Override
   default Double next() {
      return this.nextDouble();
   }

   default void forEachRemaining(DoubleConsumer action) {
      this.forEachRemaining((java.util.function.DoubleConsumer)action);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Double> action) {
      this.forEachRemaining(action instanceof java.util.function.DoubleConsumer ? (java.util.function.DoubleConsumer)action : action::accept);
   }

   default int skip(int n) {
      if (n < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + n);
      } else {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextDouble();
         }

         return n - i - 1;
      }
   }
}
