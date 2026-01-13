package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface CharUnaryOperator extends UnaryOperator<Character>, IntUnaryOperator {
   char apply(char var1);

   static CharUnaryOperator identity() {
      return i -> i;
   }

   @Deprecated
   @Override
   default int applyAsInt(int x) {
      return this.apply(SafeMath.safeIntToChar(x));
   }

   @Deprecated
   default Character apply(Character x) {
      return this.apply(x.charValue());
   }
}
