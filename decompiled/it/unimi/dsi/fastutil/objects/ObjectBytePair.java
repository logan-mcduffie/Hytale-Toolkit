package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ObjectBytePair<K> extends Pair<K, Byte> {
   byte rightByte();

   @Deprecated
   default Byte right() {
      return this.rightByte();
   }

   default ObjectBytePair<K> right(byte r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ObjectBytePair<K> right(Byte l) {
      return this.right(l.byteValue());
   }

   default byte secondByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte second() {
      return this.secondByte();
   }

   default ObjectBytePair<K> second(byte r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectBytePair<K> second(Byte l) {
      return this.second(l.byteValue());
   }

   default byte valueByte() {
      return this.rightByte();
   }

   @Deprecated
   default Byte value() {
      return this.valueByte();
   }

   default ObjectBytePair<K> value(byte r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectBytePair<K> value(Byte l) {
      return this.value(l.byteValue());
   }

   static <K> ObjectBytePair<K> of(K left, byte right) {
      return new ObjectByteImmutablePair<>(left, right);
   }

   static <K> Comparator<ObjectBytePair<K>> lexComparator() {
      return (x, y) -> {
         int t = ((Comparable)x.left()).compareTo(y.left());
         return t != 0 ? t : Byte.compare(x.rightByte(), y.rightByte());
      };
   }
}
