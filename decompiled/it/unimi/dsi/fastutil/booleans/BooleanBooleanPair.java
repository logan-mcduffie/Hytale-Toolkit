package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanBooleanPair extends Pair<Boolean, Boolean> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanBooleanPair left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanBooleanPair left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanBooleanPair first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanBooleanPair first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanBooleanPair key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanBooleanPair key(Boolean l) {
      return this.key(l.booleanValue());
   }

   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default BooleanBooleanPair right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanBooleanPair right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default BooleanBooleanPair second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanBooleanPair second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default BooleanBooleanPair value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanBooleanPair value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static BooleanBooleanPair of(boolean left, boolean right) {
      return new BooleanBooleanImmutablePair(left, right);
   }

   static Comparator<BooleanBooleanPair> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
