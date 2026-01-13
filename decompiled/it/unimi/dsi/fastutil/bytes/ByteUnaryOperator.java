package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface ByteUnaryOperator extends UnaryOperator<Byte>, IntUnaryOperator {
   byte apply(byte var1);

   static ByteUnaryOperator identity() {
      return i -> i;
   }

   static ByteUnaryOperator negation() {
      return i -> (byte)(-i);
   }

   @Deprecated
   @Override
   default int applyAsInt(int x) {
      return this.apply(SafeMath.safeIntToByte(x));
   }

   @Deprecated
   default Byte apply(Byte x) {
      return this.apply(x.byteValue());
   }
}
