package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface ByteBinaryOperator extends BinaryOperator<Byte>, IntBinaryOperator {
   byte apply(byte var1, byte var2);

   @Deprecated
   @Override
   default int applyAsInt(int x, int y) {
      return this.apply(SafeMath.safeIntToByte(x), SafeMath.safeIntToByte(y));
   }

   @Deprecated
   default Byte apply(Byte x, Byte y) {
      return this.apply(x.byteValue(), y.byteValue());
   }
}
