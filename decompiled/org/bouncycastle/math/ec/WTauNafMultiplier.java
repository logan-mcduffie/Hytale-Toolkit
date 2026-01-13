package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class WTauNafMultiplier extends AbstractECMultiplier {
   static final String PRECOMP_NAME = "bc_wtnaf";

   @Override
   protected ECPoint multiplyPositive(ECPoint var1, BigInteger var2) {
      if (!(var1 instanceof ECPoint.AbstractF2m)) {
         throw new IllegalArgumentException("Only ECPoint.AbstractF2m can be used in WTauNafMultiplier");
      } else {
         ECPoint.AbstractF2m var3 = (ECPoint.AbstractF2m)var1;
         ECCurve.AbstractF2m var4 = (ECCurve.AbstractF2m)var3.getCurve();
         byte var5 = var4.getA().toBigInteger().byteValue();
         byte var6 = Tnaf.getMu(var5);
         ZTauElement var7 = Tnaf.partModReduction(var4, var2, var5, var6, (byte)10);
         return this.multiplyWTnaf(var3, var7, var5, var6);
      }
   }

   private ECPoint.AbstractF2m multiplyWTnaf(ECPoint.AbstractF2m var1, ZTauElement var2, byte var3, byte var4) {
      ZTauElement[] var5 = var3 == 0 ? Tnaf.alpha0 : Tnaf.alpha1;
      BigInteger var6 = Tnaf.getTw(var4, 4);
      byte[] var7 = Tnaf.tauAdicWNaf(var4, var2, 4, var6.intValue(), var5);
      return multiplyFromWTnaf(var1, var7);
   }

   private static ECPoint.AbstractF2m multiplyFromWTnaf(final ECPoint.AbstractF2m var0, byte[] var1) {
      ECCurve.AbstractF2m var2 = (ECCurve.AbstractF2m)var0.getCurve();
      final byte var3 = var2.getA().toBigInteger().byteValue();
      WTauNafPreCompInfo var4 = (WTauNafPreCompInfo)var2.precompute(var0, "bc_wtnaf", new PreCompCallback() {
         @Override
         public PreCompInfo precompute(PreCompInfo var1) {
            if (var1 instanceof WTauNafPreCompInfo) {
               return var1;
            } else {
               WTauNafPreCompInfo var2x = new WTauNafPreCompInfo();
               var2x.setPreComp(Tnaf.getPreComp(var0, var3));
               return var2x;
            }
         }
      });
      ECPoint.AbstractF2m[] var5 = var4.getPreComp();
      ECPoint.AbstractF2m[] var6 = new ECPoint.AbstractF2m[var5.length];

      for (int var7 = 0; var7 < var5.length; var7++) {
         var6[var7] = (ECPoint.AbstractF2m)var5[var7].negate();
      }

      ECPoint.AbstractF2m var12 = (ECPoint.AbstractF2m)var0.getCurve().getInfinity();
      int var8 = 0;

      for (int var9 = var1.length - 1; var9 >= 0; var9--) {
         var8++;
         byte var10 = var1[var9];
         if (var10 != 0) {
            var12 = var12.tauPow(var8);
            var8 = 0;
            ECPoint.AbstractF2m var11 = var10 > 0 ? var5[var10 >>> 1] : var6[-var10 >>> 1];
            var12 = (ECPoint.AbstractF2m)var12.add(var11);
         }
      }

      if (var8 > 0) {
         var12 = var12.tauPow(var8);
      }

      return var12;
   }
}
