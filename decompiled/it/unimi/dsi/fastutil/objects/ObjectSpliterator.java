package it.unimi.dsi.fastutil.objects;

import java.util.Spliterator;

public interface ObjectSpliterator<K> extends Spliterator<K> {
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

   ObjectSpliterator<K> trySplit();
}
