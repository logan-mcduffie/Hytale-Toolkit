package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceFloatPair<K> extends Pair<K, Float> {
   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default ReferenceFloatPair<K> right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ReferenceFloatPair<K> right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default ReferenceFloatPair<K> second(float r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceFloatPair<K> second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default ReferenceFloatPair<K> value(float r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceFloatPair<K> value(Float l) {
      return this.value(l.floatValue());
   }

   static <K> ReferenceFloatPair<K> of(K left, float right) {
      return new ReferenceFloatImmutablePair<>(left, right);
   }
}
