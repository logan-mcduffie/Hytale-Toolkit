package it.unimi.dsi.fastutil.doubles;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface DoublePredicate extends Predicate<Double>, java.util.function.DoublePredicate {
   @Deprecated
   default boolean test(Double t) {
      return this.test(t.doubleValue());
   }

   default DoublePredicate and(java.util.function.DoublePredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) && other.test(t);
   }

   default DoublePredicate and(DoublePredicate other) {
      return this.and((java.util.function.DoublePredicate)other);
   }

   @Deprecated
   @Override
   default Predicate<Double> and(Predicate<? super Double> other) {
      return Predicate.super.and(other);
   }

   default DoublePredicate negate() {
      return t -> !this.test(t);
   }

   default DoublePredicate or(java.util.function.DoublePredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) || other.test(t);
   }

   default DoublePredicate or(DoublePredicate other) {
      return this.or((java.util.function.DoublePredicate)other);
   }

   @Deprecated
   @Override
   default Predicate<Double> or(Predicate<? super Double> other) {
      return Predicate.super.or(other);
   }
}
