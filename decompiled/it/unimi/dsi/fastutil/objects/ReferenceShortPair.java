package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceShortPair<K> extends Pair<K, Short> {
   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default ReferenceShortPair<K> right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ReferenceShortPair<K> right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default ReferenceShortPair<K> second(short r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceShortPair<K> second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default ReferenceShortPair<K> value(short r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceShortPair<K> value(Short l) {
      return this.value(l.shortValue());
   }

   static <K> ReferenceShortPair<K> of(K left, short right) {
      return new ReferenceShortImmutablePair<>(left, right);
   }
}
