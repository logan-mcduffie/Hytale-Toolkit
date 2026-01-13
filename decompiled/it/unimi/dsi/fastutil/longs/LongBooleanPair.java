package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface LongBooleanPair extends Pair<Long, Boolean> {
   long leftLong();

   @Deprecated
   default Long left() {
      return this.leftLong();
   }

   default LongBooleanPair left(long l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongBooleanPair left(Long l) {
      return this.left(l.longValue());
   }

   default long firstLong() {
      return this.leftLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   default LongBooleanPair first(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongBooleanPair first(Long l) {
      return this.first(l.longValue());
   }

   default long keyLong() {
      return this.firstLong();
   }

   @Deprecated
   default Long key() {
      return this.keyLong();
   }

   default LongBooleanPair key(long l) {
      return this.left(l);
   }

   @Deprecated
   default LongBooleanPair key(Long l) {
      return this.key(l.longValue());
   }

   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default LongBooleanPair right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default LongBooleanPair right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default LongBooleanPair second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default LongBooleanPair second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default LongBooleanPair value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default LongBooleanPair value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static LongBooleanPair of(long left, boolean right) {
      return new LongBooleanImmutablePair(left, right);
   }

   static Comparator<LongBooleanPair> lexComparator() {
      return (x, y) -> {
         int t = Long.compare(x.leftLong(), y.leftLong());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
