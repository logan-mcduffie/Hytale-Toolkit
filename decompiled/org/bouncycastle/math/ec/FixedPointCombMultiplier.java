package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;

public class FixedPointCombMultiplier extends AbstractECMultiplier {
   @Override
   protected ECPoint multiplyPositive(ECPoint var1, BigInteger var2) {
      ECCurve var3 = var1.getCurve();
      int var4 = FixedPointUtil.getCombSize(var3);
      if (var2.bitLength() > var4) {
         throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order");
      } else {
         FixedPointPreCompInfo var5 = FixedPointUtil.precompute(var1);
         ECLookupTable var6 = var5.getLookupTable();
         int var7 = var5.getWidth();
         int var8 = (var4 + var7 - 1) / var7;
         ECPoint var9 = var3.getInfinity();
         int var10 = var8 * var7;
         int[] var11 = Nat.fromBigInteger(var10, var2);
         int var12 = var10 - 1;

         for (int var13 = 0; var13 < var8; var13++) {
            int var14 = 0;

            for (int var15 = var12 - var13; var15 >= 0; var15 -= var8) {
               int var16 = var11[var15 >>> 5] >>> (var15 & 31);
               var14 ^= var16 >>> 1;
               var14 <<= 1;
               var14 ^= var16;
            }

            ECPoint var19 = var6.lookup(var14);
            var9 = var9.twicePlus(var19);
         }

         return var9.add(var5.getOffset());
      }
   }
}
