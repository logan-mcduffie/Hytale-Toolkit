package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.math.ec.ECPoint;

public class DualECPoints {
   private final ECPoint p;
   private final ECPoint q;
   private final int securityStrength;
   private final int cofactor;

   public DualECPoints(int var1, ECPoint var2, ECPoint var3, int var4) {
      if (!var2.getCurve().equals(var3.getCurve())) {
         throw new IllegalArgumentException("points need to be on the same curve");
      } else {
         this.securityStrength = var1;
         this.p = var2;
         this.q = var3;
         this.cofactor = var4;
      }
   }

   public int getSeedLen() {
      return this.p.getCurve().getFieldSize();
   }

   public int getMaxOutlen() {
      return (this.p.getCurve().getFieldSize() - (13 + log2(this.cofactor))) / 8 * 8;
   }

   public ECPoint getP() {
      return this.p;
   }

   public ECPoint getQ() {
      return this.q;
   }

   public int getSecurityStrength() {
      return this.securityStrength;
   }

   public int getCofactor() {
      return this.cofactor;
   }

   private static int log2(int var0) {
      int var1 = 0;

      while ((var0 >>= 1) != 0) {
         var1++;
      }

      return var1;
   }
}
