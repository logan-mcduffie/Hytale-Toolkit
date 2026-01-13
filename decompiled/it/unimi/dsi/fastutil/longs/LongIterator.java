package it.unimi.dsi.fastutil.longs;

import java.util.PrimitiveIterator.OfLong;
import java.util.function.Consumer;

public interface LongIterator extends OfLong {
   @Override
   long nextLong();

   @Deprecated
   @Override
   default Long next() {
      return this.nextLong();
   }

   default void forEachRemaining(LongConsumer action) {
      this.forEachRemaining((java.util.function.LongConsumer)action);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Long> action) {
      this.forEachRemaining(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)action : action::accept);
   }

   default int skip(int n) {
      if (n < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + n);
      } else {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextLong();
         }

         return n - i - 1;
      }
   }
}
