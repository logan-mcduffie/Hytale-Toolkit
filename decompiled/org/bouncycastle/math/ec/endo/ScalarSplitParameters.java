package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;

public class ScalarSplitParameters {
   protected final BigInteger v1A;
   protected final BigInteger v1B;
   protected final BigInteger v2A;
   protected final BigInteger v2B;
   protected final BigInteger g1;
   protected final BigInteger g2;
   protected final int bits;

   private static void checkVector(BigInteger[] var0, String var1) {
      if (var0 == null || var0.length != 2 || var0[0] == null || var0[1] == null) {
         throw new IllegalArgumentException("'" + var1 + "' must consist of exactly 2 (non-null) values");
      }
   }

   public ScalarSplitParameters(BigInteger[] var1, BigInteger[] var2, BigInteger var3, BigInteger var4, int var5) {
      checkVector(var1, "v1");
      checkVector(var2, "v2");
      this.v1A = var1[0];
      this.v1B = var1[1];
      this.v2A = var2[0];
      this.v2B = var2[1];
      this.g1 = var3;
      this.g2 = var4;
      this.bits = var5;
   }

   public BigInteger getV1A() {
      return this.v1A;
   }

   public BigInteger getV1B() {
      return this.v1B;
   }

   public BigInteger getV2A() {
      return this.v2A;
   }

   public BigInteger getV2B() {
      return this.v2B;
   }

   public BigInteger getG1() {
      return this.g1;
   }

   public BigInteger getG2() {
      return this.g2;
   }

   public int getBits() {
      return this.bits;
   }
}
