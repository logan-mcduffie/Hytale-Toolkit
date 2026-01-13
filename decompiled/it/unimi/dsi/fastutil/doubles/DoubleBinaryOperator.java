package it.unimi.dsi.fastutil.doubles;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface DoubleBinaryOperator extends BinaryOperator<Double>, java.util.function.DoubleBinaryOperator {
   double apply(double var1, double var3);

   @Deprecated
   @Override
   default double applyAsDouble(double x, double y) {
      return this.apply(x, y);
   }

   @Deprecated
   default Double apply(Double x, Double y) {
      return this.apply(x.doubleValue(), y.doubleValue());
   }
}
