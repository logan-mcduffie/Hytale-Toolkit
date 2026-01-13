package it.unimi.dsi.fastutil.longs;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface LongBinaryOperator extends BinaryOperator<Long>, java.util.function.LongBinaryOperator {
   long apply(long var1, long var3);

   @Deprecated
   @Override
   default long applyAsLong(long x, long y) {
      return this.apply(x, y);
   }

   @Deprecated
   default Long apply(Long x, Long y) {
      return this.apply(x.longValue(), y.longValue());
   }
}
