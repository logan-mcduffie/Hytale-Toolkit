package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface CharPredicate extends Predicate<Character>, IntPredicate {
   boolean test(char var1);

   @Deprecated
   @Override
   default boolean test(int t) {
      return this.test(SafeMath.safeIntToChar(t));
   }

   @Deprecated
   default boolean test(Character t) {
      return this.test(t.charValue());
   }

   default CharPredicate and(CharPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) && other.test(t);
   }

   default CharPredicate and(IntPredicate other) {
      return this.and(other instanceof CharPredicate ? (CharPredicate)other : other::test);
   }

   @Deprecated
   @Override
   default Predicate<Character> and(Predicate<? super Character> other) {
      return Predicate.super.and(other);
   }

   default CharPredicate negate() {
      return t -> !this.test(t);
   }

   default CharPredicate or(CharPredicate other) {
      Objects.requireNonNull(other);
      return t -> this.test(t) || other.test(t);
   }

   default CharPredicate or(IntPredicate other) {
      return this.or(other instanceof CharPredicate ? (CharPredicate)other : other::test);
   }

   @Deprecated
   @Override
   default Predicate<Character> or(Predicate<? super Character> other) {
      return Predicate.super.or(other);
   }
}
