package it.unimi.dsi.fastutil.chars;

import java.util.Spliterator.OfPrimitive;
import java.util.function.Consumer;

public interface CharSpliterator extends OfPrimitive<Character, CharConsumer, CharSpliterator> {
   @Deprecated
   @Override
   default boolean tryAdvance(Consumer<? super Character> action) {
      return this.tryAdvance(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Character> action) {
      this.forEachRemaining(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
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

   CharSpliterator trySplit();

   default CharComparator getComparator() {
      throw new IllegalStateException();
   }
}
