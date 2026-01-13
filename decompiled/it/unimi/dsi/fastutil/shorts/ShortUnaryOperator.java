package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface ShortUnaryOperator extends UnaryOperator<Short>, IntUnaryOperator {
   short apply(short var1);

   static ShortUnaryOperator identity() {
      return i -> i;
   }

   static ShortUnaryOperator negation() {
      return i -> (short)(-i);
   }

   @Deprecated
   @Override
   default int applyAsInt(int x) {
      return this.apply(SafeMath.safeIntToShort(x));
   }

   @Deprecated
   default Short apply(Short x) {
      return this.apply(x.shortValue());
   }
}
