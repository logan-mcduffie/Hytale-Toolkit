package it.unimi.dsi.fastutil.booleans;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface BooleanBinaryOperator extends BinaryOperator<Boolean> {
   boolean apply(boolean var1, boolean var2);

   @Deprecated
   default Boolean apply(Boolean x, Boolean y) {
      return this.apply(x.booleanValue(), y.booleanValue());
   }
}
