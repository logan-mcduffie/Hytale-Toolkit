package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ObjectBooleanPair<K> extends Pair<K, Boolean> {
   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default ObjectBooleanPair<K> right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ObjectBooleanPair<K> right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default ObjectBooleanPair<K> second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectBooleanPair<K> second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default ObjectBooleanPair<K> value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectBooleanPair<K> value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static <K> ObjectBooleanPair<K> of(K left, boolean right) {
      return new ObjectBooleanImmutablePair<>(left, right);
   }

   static <K> Comparator<ObjectBooleanPair<K>> lexComparator() {
      return (x, y) -> {
         int t = ((Comparable)x.left()).compareTo(y.left());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
