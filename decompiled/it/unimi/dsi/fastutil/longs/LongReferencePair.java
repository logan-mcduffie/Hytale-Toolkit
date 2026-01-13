package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;

public interface LongReferencePair<V> extends Pair<Long, V> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongReferencePair<V> left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongReferencePair<V> left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongReferencePair<V> first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongReferencePair<V> first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongReferencePair<V> key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongReferencePair<V> key(Long l) {
      return this.key(l.longValue());
   }

   static <V> LongReferencePair<V> of(long left, V right) {
      return new LongReferenceImmutablePair<>(left, right);
   }
}
