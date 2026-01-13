package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongObjectPair<V> extends Pair<Long, V> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongObjectPair<V> left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongObjectPair<V> left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongObjectPair<V> first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongObjectPair<V> first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongObjectPair<V> key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongObjectPair<V> key(Long l) {
      return this.key(l.longValue());
   }

   static <V> LongObjectPair<V> of(long left, V right) {
      return new LongObjectImmutablePair<>(left, right);
   }

   static <V> Comparator<LongObjectPair<V>> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : ((Comparable)x.right()).compareTo(y.right());
      };
   }
}
