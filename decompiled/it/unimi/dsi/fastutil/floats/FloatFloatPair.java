package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import java.util.Comparator;

public interface FloatFloatPair extends Pair<Float, Float> {
   float leftFloat();

   @Deprecated
   default Float left() {
      return this.leftFloat();
   }

   default FloatFloatPair left(float l) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatFloatPair left(Float l) {
      return this.left(l.floatValue());
   }

   default float firstFloat() {
      return this.leftFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   default FloatFloatPair first(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatFloatPair first(Float l) {
      return this.first(l.floatValue());
   }

   default float keyFloat() {
      return this.firstFloat();
   }

   @Deprecated
   default Float key() {
      return this.keyFloat();
   }

   default FloatFloatPair key(float l) {
      return this.left(l);
   }

   @Deprecated
   default FloatFloatPair key(Float l) {
      return this.key(l.floatValue());
   }

   float rightFloat();

   @Deprecated
   default Float right() {
      return this.rightFloat();
   }

   default FloatFloatPair right(float r) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default FloatFloatPair right(Float l) {
      return this.right(l.floatValue());
   }

   default float secondFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float second() {
      return this.secondFloat();
   }

   default FloatFloatPair second(float r) {
      return this.right(r);
   }

   @Deprecated
   default FloatFloatPair second(Float l) {
      return this.second(l.floatValue());
   }

   default float valueFloat() {
      return this.rightFloat();
   }

   @Deprecated
   default Float value() {
      return this.valueFloat();
   }

   default FloatFloatPair value(float r) {
      return this.right(r);
   }

   @Deprecated
   default FloatFloatPair value(Float l) {
      return this.value(l.floatValue());
   }

   static FloatFloatPair of(float left, float right) {
      return new FloatFloatImmutablePair(left, right);
   }

   static Comparator<FloatFloatPair> lexComparator() {
      return (x, y) -> {
         int t = Float.compare(x.leftFloat(), y.leftFloat());
         return t != 0 ? t : Float.compare(x.rightFloat(), y.rightFloat());
      };
   }
}
