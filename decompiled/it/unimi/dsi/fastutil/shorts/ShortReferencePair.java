package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;

public interface ShortReferencePair<V> extends Pair<Short, V> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortReferencePair<V> left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortReferencePair<V> left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortReferencePair<V> first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortReferencePair<V> first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortReferencePair<V> key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortReferencePair<V> key(Short l) {
      return this.key(l.shortValue());
   }

   static <V> ShortReferencePair<V> of(short left, V right) {
      return new ShortReferenceImmutablePair<>(left, right);
   }
}
