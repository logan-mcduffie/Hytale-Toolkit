package it.unimi.dsi.fastutil.longs;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface LongUnaryOperator extends UnaryOperator<Long>, java.util.function.LongUnaryOperator {
   long apply(long var1);

   static LongUnaryOperator identity() {
      return i -> i;
   }

   static LongUnaryOperator negation() {
      return i -> -i;
   }

   @Deprecated
   @Override
   default long applyAsLong(long x) {
      return this.apply(x);
   }

   @Deprecated
   default Long apply(Long x) {
      return this.apply(x.longValue());
   }
}
