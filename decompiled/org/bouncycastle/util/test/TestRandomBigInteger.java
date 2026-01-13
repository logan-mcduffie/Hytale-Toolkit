package org.bouncycastle.util.test;

import org.bouncycastle.util.BigIntegers;

public class TestRandomBigInteger extends FixedSecureRandom {
   public TestRandomBigInteger(String var1) {
      this(var1, 10);
   }

   public TestRandomBigInteger(String var1, int var2) {
      super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(BigIntegers.asUnsignedByteArray(new java.math.BigInteger(var1, var2)))});
   }

   public TestRandomBigInteger(byte[] var1) {
      super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(var1)});
   }

   public TestRandomBigInteger(int var1, byte[] var2) {
      super(new FixedSecureRandom.Source[]{new FixedSecureRandom.BigInteger(var1, var2)});
   }
}
