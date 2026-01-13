package it.unimi.dsi.fastutil.bytes;

import java.util.Spliterator.OfPrimitive;
import java.util.function.Consumer;

public interface ByteSpliterator extends OfPrimitive<Byte, ByteConsumer, ByteSpliterator> {
   @Deprecated
   @Override
   default boolean tryAdvance(Consumer<? super Byte> action) {
      return this.tryAdvance(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Byte> action) {
      this.forEachRemaining(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
   }

   default long skip(long n) {
      if (n < 0L) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + n);
      } else {
         long i = n;

         while (i-- != 0L && this.tryAdvance(unused -> {})) {
         }

         return n - i - 1L;
      }
   }

   ByteSpliterator trySplit();

   default ByteComparator getComparator() {
      throw new IllegalStateException();
   }
}
