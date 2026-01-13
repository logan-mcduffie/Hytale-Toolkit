package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface BooleanFloatPair extends Pair<Boolean, Float> {
   boolean leftBoolean();

   @Deprecated
   default Boolean left() {
      return this.leftBoolean();
   }

   default BooleanFloatPair left(boolean l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanFloatPair left(Boolean l) {
      return this.left(l.booleanValue());
   }

   default boolean firstBoolean() {
      return this.leftBoolean();
   }

   @Deprecated
   default Boolean first() {
      return this.firstBoolean();
   }

   default BooleanFloatPair first(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanFloatPair first(Boolean l) {
      return this.first(l.booleanValue());
   }

   default boolean keyBoolean() {
      return this.firstBoolean();
   }

   @Deprecated
   default Boolean key() {
      return this.keyBoolean();
   }

   default BooleanFloatPair key(boolean l) {
      return this.left(l);
   }

   @Deprecated
   default BooleanFloatPair key(Boolean l) {
      return this.key(l.booleanValue());
   }

   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default BooleanFloatPair right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default BooleanFloatPair right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default BooleanFloatPair second(float r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanFloatPair second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default BooleanFloatPair value(float r) {
      return this.right(r);
   }

   @Deprecated
   default BooleanFloatPair value(Float l) {
      return this.value(l.floatValue());
   }

   static BooleanFloatPair of(boolean left, float right) {
      return new BooleanFloatImmutablePair(left, right);
   }

   static Comparator<BooleanFloatPair> lexComparator() {
      return (x, y) -> {
         int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
