package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface ShortBinaryOperator extends BinaryOperator<Short>, IntBinaryOperator {
   short apply(short var1, short var2);

   @Deprecated
   @Override
   default int applyAsInt(int x, int y) {
      return this.apply(SafeMath.safeIntToShort(x), SafeMath.safeIntToShort(y));
   }

   @Deprecated
   default Short apply(Short x, Short y) {
      return this.apply(x.shortValue(), y.shortValue());
   }
}
