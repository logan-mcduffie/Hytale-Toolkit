package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface LongPredicate extends Predicate<Long>, java.util.function.LongPredicate {
   @Deprecated
   default boolean test(Long t) {
      return this.test(t.longValue());
   }

   default LongPredicate and(java.util.function.LongPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) && other.test(t);
   }

   default LongPredicate and(LongPredicate other) {
      return this.and((java.util.function.LongPredicate)other);
   }

   @Deprecated
   @Override
   default Predicate<Long> and(Predicate<? super Long> other) {
      return Predicate.super.and(other);
   }

   default LongPredicate negate() {
      return t -> !this.test(t);
   }

   default LongPredicate or(java.util.function.LongPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) || other.test(t);
   }

   default LongPredicate or(LongPredicate other) {
      return this.or((java.util.function.LongPredicate)other);
   }

   @Deprecated
   @Override
   default Predicate<Long> or(Predicate<? super Long> other) {
      return Predicate.super.or(other);
   }
}
