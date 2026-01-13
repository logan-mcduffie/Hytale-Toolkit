package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanLongPair extends Pair<Boolean, Long> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanLongPair left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanLongPair left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanLongPair first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanLongPair first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanLongPair key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanLongPair key(Boolean l) {
      return this.key(l.booleanValue());
   }

   long rightLong();

   @Deprecated
   default Long right() {
      return this.rightLong();
   }

   default BooleanLongPair right(long r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanLongPair right(Long l) {
      return this.right(l.longValue());
   }

   default long secondLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long second() {
      return this.secondLong();
   }

   default BooleanLongPair second(long r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanLongPair second(Long l) {
      return this.second(l.longValue());
   }

   default long valueLong() {
      return this.rightLong();
   }

   @Deprecated
   default Long value() {
      return this.valueLong();
   }

   default BooleanLongPair value(long r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanLongPair value(Long l) {
      return this.value(l.longValue());
   }

   static BooleanLongPair of(boolean left, long right) {
      return new BooleanLongImmutablePair(left, right);
   }

   static Comparator<BooleanLongPair> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : Long.compare(x.rightLong(), y.rightLong());
      };
   }
}
