package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanIntPair extends Pair<Boolean, Integer> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanIntPair left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanIntPair left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanIntPair first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanIntPair first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanIntPair key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanIntPair key(Boolean l) {
      return this.key(l.booleanValue());
   }

   int rightInt();

   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default BooleanIntPair right(int r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanIntPair right(Integer l) {
      return this.right(l.intValue());
   }

   default int secondInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default BooleanIntPair second(int r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanIntPair second(Integer l) {
      return this.second(l.intValue());
   }

   default int valueInt() {
      return this.rightInt();
   }

   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default BooleanIntPair value(int r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanIntPair value(Integer l) {
      return this.value(l.intValue());
   }

   static BooleanIntPair of(boolean left, int right) {
      return new BooleanIntImmutablePair(left, right);
   }

   static Comparator<BooleanIntPair> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
