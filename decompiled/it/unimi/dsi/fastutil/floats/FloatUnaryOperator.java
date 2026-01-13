package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface FloatUnaryOperator extends UnaryOperator<Float>, DoubleUnaryOperator {
   float apply(float var1);

   static FloatUnaryOperator identity() {
      return i -> i;
   }

   static FloatUnaryOperator negation() {
      return i -> -i;
   }

   @Deprecated
   @Override
   default double applyAsDouble(double x) {
      return this.apply(SafeMath.safeDoubleToFloat(x));
   }

   @Deprecated
   default Float apply(Float x) {
      return this.apply(x.floatValue());
   }
}
