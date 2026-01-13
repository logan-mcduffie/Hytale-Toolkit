package it.unimi.dsi.fastutil.booleans;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface BooleanConsumer extends Consumer<Boolean> {
   void accept(boolean var1);

   @Deprecated
   default void accept(Boolean t) {
      this.accept(t.booleanValue());
   }

   default BooleanConsumer andThen(BooleanConsumer after) {
      Objects.requireNonNull(after);
      return t -> {
         this.accept(t);
         after.accept(t);
      };
   }

   @Deprecated
   @Override
   default Consumer<Boolean> andThen(Consumer<? super Boolean> after) {
      return Consumer.super.andThen(after);
   }
}
