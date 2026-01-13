package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatBooleanPair extends Pair<Float, Boolean> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatBooleanPair left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatBooleanPair left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatBooleanPair first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatBooleanPair first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatBooleanPair key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatBooleanPair key(Float l) {
      return this.key(l.floatValue());
   }

   boolean rightBoolean();

   @Deprecated
   default Boolean right() {
      return this.rightBoolean();
   }

   default FloatBooleanPair right(boolean r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatBooleanPair right(Boolean l) {
      return this.right(l.booleanValue());
   }

   default boolean secondBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean second() {
      return this.secondBoolean();
   }

   default FloatBooleanPair second(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default FloatBooleanPair second(Boolean l) {
      return this.second(l.booleanValue());
   }

   default boolean valueBoolean() {
      return this.rightBoolean();
   }

   @Deprecated
   default Boolean value() {
      return this.valueBoolean();
   }

   default FloatBooleanPair value(boolean r) {
      return this.right(r);
   }

   @Deprecated
   default FloatBooleanPair value(Boolean l) {
      return this.value(l.booleanValue());
   }

   static FloatBooleanPair of(float left, boolean right) {
      return new FloatBooleanImmutablePair(left, right);
   }

   static Comparator<FloatBooleanPair> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : Boolean.compare(x.rightBoolean(), y.rightBoolean());
      };
   }
}
