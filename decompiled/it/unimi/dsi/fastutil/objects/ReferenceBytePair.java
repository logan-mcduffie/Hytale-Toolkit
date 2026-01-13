package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceBytePair<K> extends Pair<K, Byte> {
   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default ReferenceBytePair<K> right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ReferenceBytePair<K> right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default ReferenceBytePair<K> second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceBytePair<K> second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default ReferenceBytePair<K> value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceBytePair<K> value(Byte l) {
      return this.value(l.byteValue());
   }

   static <K> ReferenceBytePair<K> of(K left, byte right) {
      return new ReferenceByteImmutablePair<>(left, right);
   }
}
