package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;

public interface BooleanReferencePair<V> extends Pair<Boolean, V> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanReferencePair<V> left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanReferencePair<V> left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanReferencePair<V> first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanReferencePair<V> first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanReferencePair<V> key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanReferencePair<V> key(Boolean l) {
      return this.key(l.booleanValue());
   }

   static <V> BooleanReferencePair<V> of(boolean left, V right) {
      return new BooleanReferenceImmutablePair<>(left, right);
   }
}
