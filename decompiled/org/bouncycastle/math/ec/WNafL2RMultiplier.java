package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.util.Integers;

public class WNafL2RMultiplier extends AbstractECMultiplier {
   @Override
   protected ECPoint multiplyPositive(ECPoint var1, BigInteger var2) {
      int var3 = WNafUtil.getWindowSize(var2.bitLength());
      WNafPreCompInfo var4 = WNafUtil.precompute(var1, var3, true);
      ECPoint[] var5 = var4.getPreComp();
      ECPoint[] var6 = var4.getPreCompNeg();
      int var7 = var4.getWidth();
      int[] var8 = WNafUtil.generateCompactWindowNaf(var7, var2);
      ECPoint var9 = var1.getCurve().getInfinity();
      int var10 = var8.length;
      if (var10 > 1) {
         int var11 = var8[--var10];
         int var12 = var11 >> 16;
         int var13 = var11 & 65535;
         int var14 = Math.abs(var12);
         ECPoint[] var15 = var12 < 0 ? var6 : var5;
         if (var14 << 2 < 1 << var7) {
            int var16 = 32 - Integers.numberOfLeadingZeros(var14);
            int var17 = var7 - var16;
            int var18 = var14 ^ 1 << var16 - 1;
            int var19 = (1 << var7 - 1) - 1;
            int var20 = (var18 << var17) + 1;
            var9 = var15[var19 >>> 1].add(var15[var20 >>> 1]);
            var13 -= var17;
         } else {
            var9 = var15[var14 >>> 1];
         }

         var9 = var9.timesPow2(var13);
      }

      while (var10 > 0) {
         int var23 = var8[--var10];
         int var24 = var23 >> 16;
         int var25 = var23 & 65535;
         int var26 = Math.abs(var24);
         ECPoint[] var27 = var24 < 0 ? var6 : var5;
         ECPoint var28 = var27[var26 >>> 1];
         var9 = var9.twicePlus(var28);
         var9 = var9.timesPow2(var25);
      }

      return var9;
   }
}
