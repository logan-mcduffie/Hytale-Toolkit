package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ShortConsumer extends Consumer<Short>, IntConsumer {
   void accept(short var1);

   @Deprecated
   @Override
   default void accept(int t) {
      this.accept(SafeMath.safeIntToShort(t));
   }

   @Deprecated
   default void accept(Short t) {
      this.accept(t.shortValue());
   }

   default ShortConsumer andThen(ShortConsumer after) {
      Objects.requireNonNull(after);
      return t -> {
         this.accept(t);
         after.accept(t);
      };
   }

   default ShortConsumer andThen(IntConsumer after) {
      return this.andThen(after instanceof ShortConsumer ? (ShortConsumer)after : after::accept);
   }

   @Deprecated
   @Override
   default Consumer<Short> andThen(Consumer<? super Short> after) {
      return Consumer.super.andThen(after);
   }
}
