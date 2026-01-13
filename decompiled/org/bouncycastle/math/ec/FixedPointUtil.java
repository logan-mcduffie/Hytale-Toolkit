package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class FixedPointUtil {
   public static final String PRECOMP_NAME = "bc_fixed_point";

   public static int getCombSize(ECCurve var0) {
      BigInteger var1 = var0.getOrder();
      return var1 == null ? var0.getFieldSize() + 1 : var1.bitLength();
   }

   public static FixedPointPreCompInfo getFixedPointPreCompInfo(PreCompInfo var0) {
      return var0 instanceof FixedPointPreCompInfo ? (FixedPointPreCompInfo)var0 : null;
   }

   public static FixedPointPreCompInfo precompute(final ECPoint var0) {
      final ECCurve var1 = var0.getCurve();
      return (FixedPointPreCompInfo)var1.precompute(var0, "bc_fixed_point", new PreCompCallback() {
         @Override
         public PreCompInfo precompute(PreCompInfo var1x) {
            FixedPointPreCompInfo var2 = var1x instanceof FixedPointPreCompInfo ? (FixedPointPreCompInfo)var1x : null;
            int var3 = FixedPointUtil.getCombSize(var1);
            int var4 = var3 > 250 ? 6 : 5;
            int var5 = 1 << var4;
            if (this.checkExisting(var2, var5)) {
               return var2;
            } else {
               int var6 = (var3 + var4 - 1) / var4;
               ECPoint[] var7 = new ECPoint[var4 + 1];
               var7[0] = var0;

               for (int var8 = 1; var8 < var4; var8++) {
                  var7[var8] = var7[var8 - 1].timesPow2(var6);
               }

               var7[var4] = var7[0].subtract(var7[1]);
               var1.normalizeAll(var7);
               ECPoint[] var13 = new ECPoint[var5];
               var13[0] = var7[0];

               for (int var9 = var4 - 1; var9 >= 0; var9--) {
                  ECPoint var10 = var7[var9];
                  int var11 = 1 << var9;

                  for (int var12 = var11; var12 < var5; var12 += var11 << 1) {
                     var13[var12] = var13[var12 - var11].add(var10);
                  }
               }

               var1.normalizeAll(var13);
               FixedPointPreCompInfo var14 = new FixedPointPreCompInfo();
               var14.setLookupTable(var1.createCacheSafeLookupTable(var13, 0, var13.length));
               var14.setOffset(var7[var4]);
               var14.setWidth(var4);
               return var14;
            }
         }

         private boolean checkExisting(FixedPointPreCompInfo var1x, int var2) {
            return var1x != null && this.checkTable(var1x.getLookupTable(), var2);
         }

         private boolean checkTable(ECLookupTable var1x, int var2) {
            return var1x != null && var1x.getSize() >= var2;
         }
      });
   }
}
