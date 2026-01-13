package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface BytePredicate extends Predicate<Byte>, IntPredicate {
   boolean test(byte var1);

   @Deprecated
   @Override
   default boolean test(int t) {
      return this.test(SafeMath.safeIntToByte(t));
   }

   @Deprecated
   default boolean test(Byte t) {
      return this.test(t.byteValue());
   }

   default BytePredicate and(BytePredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) && other.test(t);
   }

   default BytePredicate and(IntPredicate other) {
      return this.and(other instanceof BytePredicate ? (BytePredicate)other : other::test);
   }

   @Deprecated
   @Override
   default Predicate<Byte> and(Predicate<? super Byte> other) {
      return Predicate.super.and(other);
   }

   default BytePredicate negate() {
      return t -> !this.test(t);
   }

   default BytePredicate or(BytePredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) || other.test(t);
   }

   default BytePredicate or(IntPredicate other) {
      return this.or(other instanceof BytePredicate ? (BytePredicate)other : other::test);
   }

   @Deprecated
   @Override
   default Predicate<Byte> or(Predicate<? super Byte> other) {
      return Predicate.super.or(other);
   }
}
