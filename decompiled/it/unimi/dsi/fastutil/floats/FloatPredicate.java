package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface FloatPredicate extends Predicate<Float>, DoublePredicate {
   boolean test(float var1);

   @Deprecated
   @Override
   default boolean test(double t) {
      return this.test(SafeMath.safeDoubleToFloat(t));
   }

   @Deprecated
   default boolean test(Float t) {
      return this.test(t.floatValue());
   }

   default FloatPredicate and(FloatPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) && other.test(t);
   }

   default FloatPredicate and(DoublePredicate other) {
      return this.and(other instanceof FloatPredicate ? (FloatPredicate)other : other::test);
   }

   @Deprecated
   @Override
   default Predicate<Float> and(Predicate<? super Float> other) {
      return Predicate.super.and(other);
   }

   default FloatPredicate negate() {
      return t -> !this.test(t);
   }

   default FloatPredicate or(FloatPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) || other.test(t);
   }

   default FloatPredicate or(DoublePredicate other) {
      return this.or(other instanceof FloatPredicate ? (FloatPredicate)other : other::test);
   }

   @Deprecated
   @Override
   default Predicate<Float> or(Predicate<? super Float> other) {
      return Predicate.super.or(other);
   }
}
