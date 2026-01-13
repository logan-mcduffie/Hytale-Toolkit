package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ObjectShortPair<K> extends Pair<K, Short> {
   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default ObjectShortPair<K> right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ObjectShortPair<K> right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default ObjectShortPair<K> second(short r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectShortPair<K> second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default ObjectShortPair<K> value(short r) {
      return this.right(r);
   }

   @Deprecated
   default ObjectShortPair<K> value(Short l) {
      return this.value(l.shortValue());
   }

   static <K> ObjectShortPair<K> of(K left, short right) {
      return new ObjectShortImmutablePair<>(left, right);
   }

   static <K> Comparator<ObjectShortPair<K>> lexComparator() {
      return (x, y) -> {
         int t = ((Comparable)x.left()).compareTo(y.left());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
