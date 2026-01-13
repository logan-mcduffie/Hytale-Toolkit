package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface ShortPredicate extends Predicate<Short>, IntPredicate {
   boolean test(short var1);

   @Deprecated
   @Override
   default boolean test(int t) {
      return this.test(SafeMath.safeIntToShort(t));
   }

   @Deprecated
   default boolean test(Short t) {
      return this.test(t.shortValue());
   }

   default ShortPredicate and(ShortPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) && other.test(t);
   }

   default ShortPredicate and(IntPredicate other) {
      return this.and(other instanceof ShortPredicate ? (ShortPredicate)other : other::test);
   }

   @Deprecated
   @Override
   default Predicate<Short> and(Predicate<? super Short> other) {
      return Predicate.super.and(other);
   }

   default ShortPredicate negate() {
      return t -> !this.test(t);
   }

   default ShortPredicate or(ShortPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) || other.test(t);
   }

   default ShortPredicate or(IntPredicate other) {
      return this.or(other instanceof ShortPredicate ? (ShortPredicate)other : other::test);
   }

   @Deprecated
   @Override
   default Predicate<Short> or(Predicate<? super Short> other) {
      return Predicate.super.or(other);
   }
}
