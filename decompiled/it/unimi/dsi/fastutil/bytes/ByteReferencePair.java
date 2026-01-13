package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;

public interface ByteReferencePair<V> extends Pair<Byte, V> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteReferencePair<V> left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteReferencePair<V> left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteReferencePair<V> first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteReferencePair<V> first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteReferencePair<V> key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteReferencePair<V> key(Byte l) {
      return this.key(l.byteValue());
   }

   static <V> ByteReferencePair<V> of(byte left, V right) {
      return new ByteReferenceImmutablePair<>(left, right);
   }
}
