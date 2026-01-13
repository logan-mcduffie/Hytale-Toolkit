package it.unimi.dsi.fastutil.bytes;

import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ByteIterator extends PrimitiveIterator<Byte, ByteConsumer> {
   byte nextByte();

   @Deprecated
   default Byte next() {
      return this.nextByte();
   }

   default void forEachRemaining(ByteConsumer action) {
      Objects.requireNonNull(action);

      while (this.hasNext()) {
         action.accept(this.nextByte());
      }
   }

   default void forEachRemaining(IntConsumer action) {
      this.forEachRemaining(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
   }

   @Deprecated
   @Override
   default void forEachRemaining(Consumer<? super Byte> action) {
      this.forEachRemaining(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
   }

   default int skip(int n) {
      if (n < 0) {
         throw new IllegalArgumentException("Argument must be nonnegative: " + n);
      } else {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextByte();
         }

         return n - i - 1;
      }
   }
}
