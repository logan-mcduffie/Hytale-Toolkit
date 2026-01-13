package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ObjectFloatPair<K> extends Pair<K, Float> {
   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default ObjectFloatPair<K> right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ObjectFloatPair<K> right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default ObjectFloatPair<K> second(float r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectFloatPair<K> second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default ObjectFloatPair<K> value(float r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectFloatPair<K> value(Float l) {
      return this.value(l.floatValue());
   }

   static <K> ObjectFloatPair<K> of(K left, float right) {
      return new ObjectFloatImmutablePair<>(left, right);
   }

   static <K> Comparator<ObjectFloatPair<K>> lexComparator() {
      return (x, y) -> {
         int t = ((Comparable)x.left()).compareTo(y.left());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
