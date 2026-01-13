package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ByteObjectPair<V> extends Pair<Byte, V> {
   byte leftByte();

   @Deprecated
   default Byte left() {
      return this.leftByte();
   }

   default ByteObjectPair<V> left(byte l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ByteObjectPair<V> left(Byte l) {
      return this.left(l.byteValue());
   }

   default byte firstByte() {
      return this.leftByte();
   }

   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   default ByteObjectPair<V> first(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteObjectPair<V> first(Byte l) {
      return this.first(l.byteValue());
   }

   default byte keyByte() {
      return this.firstByte();
   }

   @Deprecated
   default Byte key() {
      return this.keyByte();
   }

   default ByteObjectPair<V> key(byte l) {
      return this.left(l);
   }

   @Deprecated
   default ByteObjectPair<V> key(Byte l) {
      return this.key(l.byteValue());
   }

   static <V> ByteObjectPair<V> of(byte left, V right) {
      return new ByteObjectImmutablePair<>(left, right);
   }

   static <V> Comparator<ByteObjectPair<V>> lexComparator() {
      return (x, y) -> {
         int t = Byte.compare(x.leftByte(), y.leftByte());
         return t != 0 ? t : ((Comparable)x.right()).compareTo(y.right());
      };
   }
}
