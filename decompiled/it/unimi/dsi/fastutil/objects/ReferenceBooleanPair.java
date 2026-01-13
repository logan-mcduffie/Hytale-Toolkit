package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceBooleanPair<K> extends Pair<K, Boolean> {
   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default ReferenceBooleanPair<K> right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ReferenceBooleanPair<K> right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default ReferenceBooleanPair<K> second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceBooleanPair<K> second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default ReferenceBooleanPair<K> value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceBooleanPair<K> value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static <K> ReferenceBooleanPair<K> of(K left, boolean right) {
      return new ReferenceBooleanImmutablePair<>(left, right);
   }
}
