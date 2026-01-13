package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanShortPair extends Pair<Boolean, Short> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanShortPair left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanShortPair left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanShortPair first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanShortPair first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanShortPair key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanShortPair key(Boolean l) {
      return this.key(l.booleanValue());
   }

   short rightShort();

   @Deprecated
   default Short right() {
      return this.rightShort();
   }

   default BooleanShortPair right(short r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanShortPair right(Short l) {
      return this.right(l.shortValue());
   }

   default short secondShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short second() {
      return this.secondShort();
   }

   default BooleanShortPair second(short r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanShortPair second(Short l) {
      return this.second(l.shortValue());
   }

   default short valueShort() {
      return this.rightShort();
   }

   @Deprecated
   default Short value() {
      return this.valueShort();
   }

   default BooleanShortPair value(short r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanShortPair value(Short l) {
      return this.value(l.shortValue());
   }

   static BooleanShortPair of(boolean left, short right) {
      return new BooleanShortImmutablePair(left, right);
   }

   static Comparator<BooleanShortPair> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : Short.compare(x.rightShort(), y.rightShort());
      };
   }
}
