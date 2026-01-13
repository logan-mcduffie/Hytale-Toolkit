package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface IntBooleanPair extends Pair<Integer, Boolean> {
   int leftInt();

   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntBooleanPair left(int l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntBooleanPair left(Integer l) {
      return this.left(l.intValue());
   }

   default int firstInt() {
      return this.leftInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntBooleanPair first(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntBooleanPair first(Integer l) {
      return this.first(l.intValue());
   }

   default int keyInt() {
      return this.firstInt();
   }

   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntBooleanPair key(int l) {
      return this.left(l);
   }

   @Deprecated
   default IntBooleanPair key(Integer l) {
      return this.key(l.intValue());
   }

   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default IntBooleanPair right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default IntBooleanPair right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default IntBooleanPair second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default IntBooleanPair second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default IntBooleanPair value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default IntBooleanPair value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static IntBooleanPair of(int left, boolean right) {
      return new IntBooleanImmutablePair(left, right);
   }

   static Comparator<IntBooleanPair> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
