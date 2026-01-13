package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface CharConsumer extends Consumer<Character>, IntConsumer {
   void accept(char var1);

   @Deprecated
   @Override
   default void accept(int t) {
      this.accept(SafeMath.safeIntToChar(t));
   }

   @Deprecated
   default void accept(Character t) {
      this.accept(t.charValue());
   }

   default CharConsumer andThen(CharConsumer after) {
      Objects.requireNonNull(after);
      return t -> {
         this.accept(t);
         after.accept(t);
      };
   }

   default CharConsumer andThen(IntConsumer after) {
      return this.andThen(after instanceof CharConsumer ? (CharConsumer)after : after::accept);
   }

   @Deprecated
   @Override
   default Consumer<Character> andThen(Consumer<? super Character> after) {
      return Consumer.super.andThen(after);
   }
}
