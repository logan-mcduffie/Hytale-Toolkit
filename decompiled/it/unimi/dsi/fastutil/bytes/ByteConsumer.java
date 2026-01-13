package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ByteConsumer extends Consumer<Byte>, IntConsumer {
   void accept(byte var1);

   @Deprecated
   @Override
   default void accept(int t) {
      this.accept(SafeMath.safeIntToByte(t));
   }

   @Deprecated
   default void accept(Byte t) {
      this.accept(t.byteValue());
   }

   default ByteConsumer andThen(ByteConsumer after) {
      Objects.requireNonNull(after);
      return t -> {
         this.accept(t);
         after.accept(t);
      };
   }

   default ByteConsumer andThen(IntConsumer after) {
      return this.andThen(after instanceof ByteConsumer ? (ByteConsumer)after : after::accept);
   }

   @Deprecated
   @Override
   default Consumer<Byte> andThen(Consumer<? super Byte> after) {
      return Consumer.super.andThen(after);
   }
}
