package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;

@FunctionalInterface
public interface FloatBinaryOperator extends BinaryOperator<Float>, DoubleBinaryOperator {
   float apply(float var1, float var2);

   @Deprecated
   @Override
   default double applyAsDouble(double x, double y) {
      return this.apply(SafeMath.safeDoubleToFloat(x), SafeMath.safeDoubleToFloat(y));
   }

   @Deprecated
   default Float apply(Float x, Float y) {
      return this.apply(x.floatValue(), y.floatValue());
   }
}
