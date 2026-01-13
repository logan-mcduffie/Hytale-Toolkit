package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;

public interface IntReferencePair<V> extends Pair<Integer, V> {
   int leftInt();

   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntReferencePair<V> left(int l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntReferencePair<V> left(Integer l) {
      return this.left(l.intValue());
   }

   default int firstInt() {
      return this.leftInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntReferencePair<V> first(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntReferencePair<V> first(Integer l) {
      return this.first(l.intValue());
   }

   default int keyInt() {
      return this.firstInt();
   }

   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntReferencePair<V> key(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntReferencePair<V> key(Integer l) {
      return this.key(l.intValue());
   }

   static <V> IntReferencePair<V> of(int left, V right) {
      return new IntReferenceImmutablePair<>(left, right);
   }
}
