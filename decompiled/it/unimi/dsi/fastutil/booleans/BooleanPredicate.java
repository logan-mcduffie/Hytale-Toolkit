package it.unimi.dsi.fastutil.booleans;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface BooleanPredicate extends Predicate<Boolean> {
   boolean test(boolean var1);

   static BooleanPredicate identity() {
      return b -> b;
   }

   static BooleanPredicate negation() {
      return b -> !b;
   }

   @Deprecated
   default boolean test(Boolean t) {
      return this.test(t.booleanValue());
   }

   default BooleanPredicate and(BooleanPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) && other.test(t);
   }

   @Deprecated
   @Override
   default Predicate<Boolean> and(Predicate<? super Boolean> other) {
      return Predicate.super.and(other);
   }

   default BooleanPredicate negate() {
      return t -> !this.test(t);
   }

   default BooleanPredicate or(BooleanPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) || other.test(t);
   }

   @Deprecated
   @Override
   default Predicate<Boolean> or(Predicate<? super Boolean> other) {
      return Predicate.super.or(other);
   }
}
