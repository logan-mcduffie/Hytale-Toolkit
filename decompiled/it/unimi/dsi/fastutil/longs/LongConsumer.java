package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface LongConsumer extends Consumer<Long>, java.util.function.LongConsumer {
   @Deprecated
   default void accept(Long t) {
      this.accept(t.longValue());
   }

   default LongConsumer andThen(java.util.function.LongConsumer after) {
      Objects.requireNonNull(after);
      return t -> {
         this.accept(t);
         after.accept(t);
      };
   }

   default LongConsumer andThen(LongConsumer after) {
      return this.andThen((java.util.function.LongConsumer)after);
   }

   @Deprecated
   @Override
   default Consumer<Long> andThen(Consumer<? super Long> after) {
      return Consumer.super.andThen(after);
   }
}
