package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatObjectPair<V> extends Pair<Float, V> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatObjectPair<V> left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatObjectPair<V> left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatObjectPair<V> first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatObjectPair<V> first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatObjectPair<V> key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatObjectPair<V> key(Float l) {
      return this.key(l.floatValue());
   }

   static <V> FloatObjectPair<V> of(float left, V right) {
      return new FloatObjectImmutablePair<>(left, right);
   }

   static <V> Comparator<FloatObjectPair<V>> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : ((Comparable)x.right()).compareTo(y.right());
      };
   }
}
