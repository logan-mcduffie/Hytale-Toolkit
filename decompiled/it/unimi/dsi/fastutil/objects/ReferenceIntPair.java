package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceIntPair<K> extends Pair<K, Integer> {
   int rightInt();

   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default ReferenceIntPair<K> right(int r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ReferenceIntPair<K> right(Integer l) {
      return this.right(l.intValue());
   }

   default int secondInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default ReferenceIntPair<K> second(int r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceIntPair<K> second(Integer l) {
      return this.second(l.intValue());
   }

   default int valueInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default ReferenceIntPair<K> value(int r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceIntPair<K> value(Integer l) {
      return this.value(l.intValue());
   }

   static <K> ReferenceIntPair<K> of(K left, int right) {
      return new ReferenceIntImmutablePair<>(left, right);
   }
}
