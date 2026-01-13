package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface CharBinaryOperator extends BinaryOperator<Character>, IntBinaryOperator {
   char apply(char var1, char var2);

   @Deprecated
   @Override
   default int applyAsInt(int x, int y) {
      return this.apply(SafeMath.safeIntToChar(x), SafeMath.safeIntToChar(y));
   }

   @Deprecated
   default Character apply(Character x, Character y) {
      return this.apply(x.charValue(), y.charValue());
   }
}
