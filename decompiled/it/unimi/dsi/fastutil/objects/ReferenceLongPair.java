package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;

public interface ReferenceLongPair<K> extends Pair<K, Long> {
   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default ReferenceLongPair<K> right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ReferenceLongPair<K> right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default ReferenceLongPair<K> second(long r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceLongPair<K> second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default ReferenceLongPair<K> value(long r) {
      return this.right(r);
   }

   @Deprecated
   default ReferenceLongPair<K> value(Long l) {
      return this.value(l.longValue());
   }

   static <K> ReferenceLongPair<K> of(K left, long right) {
      return new ReferenceLongImmutablePair<>(left, right);
   }
}
