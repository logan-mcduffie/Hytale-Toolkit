package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface ShortBooleanPair extends Pair<Short, Boolean> {
   short leftShort();

   @Deprecated
   default Short left() {
      return this.leftShort();
   }

   default ShortBooleanPair left(short l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortBooleanPair left(Short l) {
      return this.left(l.shortValue());
   }

   default short firstShort() {
      return this.leftShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   default ShortBooleanPair first(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortBooleanPair first(Short l) {
      return this.first(l.shortValue());
   }

   default short keyShort() {
      return this.firstShort();
   }

   @Deprecated
   default Short key() {
      return this.keyShort();
   }

   default ShortBooleanPair key(short l) {
      return this.left(l);
   }

   @Deprecated
   default ShortBooleanPair key(Short l) {
      return this.key(l.shortValue());
   }

   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default ShortBooleanPair right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default ShortBooleanPair right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default ShortBooleanPair second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default ShortBooleanPair second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default ShortBooleanPair value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default ShortBooleanPair value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static ShortBooleanPair of(short left, boolean right) {
      return new ShortBooleanImmutablePair(left, right);
   }

   static Comparator<ShortBooleanPair> lexComparator() {
      return (x, y) -> {
         int t = Short.compare(x.leftShort(), y.leftShort());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
