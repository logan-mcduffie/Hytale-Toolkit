package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanObjectPair<V> extends Pair<Boolean, V> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanObjectPair<V> left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanObjectPair<V> left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanObjectPair<V> first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanObjectPair<V> first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanObjectPair<V> key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanObjectPair<V> key(Boolean l) {
      return this.key(l.booleanValue());
   }

   static <V> BooleanObjectPair<V> of(boolean left, V right) {
      return new BooleanObjectImmutablePair<>(left, right);
   }

   static <V> Comparator<BooleanObjectPair<V>> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : ((Comparable)x.right()).compareTo(y.right());
      };
   }
}
