package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortObjectPair<V> extends Pair<Short, V> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortObjectPair<V> left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortObjectPair<V> left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortObjectPair<V> first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortObjectPair<V> first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortObjectPair<V> key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortObjectPair<V> key(Short l) {
      return this.key(l.shortValue());
   }

   static <V> ShortObjectPair<V> of(short left, V right) {
      return new ShortObjectImmutablePair<>(left, right);
   }

   static <V> Comparator<ShortObjectPair<V>> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : ((Comparable)x.right()).compareTo(y.right());
      };
   }
}
