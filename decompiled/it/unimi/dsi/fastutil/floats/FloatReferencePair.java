package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;

public interface FloatReferencePair<V> extends Pair<Float, V> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatReferencePair<V> left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatReferencePair<V> left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatReferencePair<V> first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatReferencePair<V> first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatReferencePair<V> key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatReferencePair<V> key(Float l) {
      return this.key(l.floatValue());
   }

   static <V> FloatReferencePair<V> of(float left, V right) {
      return new FloatReferenceImmutablePair<>(left, right);
   }
}
