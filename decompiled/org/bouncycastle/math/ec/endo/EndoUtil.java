package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.PreCompCallback;
import org.bouncycastle.math.ec.PreCompInfo;

public abstract class EndoUtil {
   public static final String PRECOMP_NAME = "bc_endo";

   public static BigInteger[] decomposeScalar(ScalarSplitParameters var0, BigInteger var1) {
      int var2 = var0.getBits();
      BigInteger var3 = calculateB(var1, var0.getG1(), var2);
      BigInteger var4 = calculateB(var1, var0.getG2(), var2);
      BigInteger var5 = var1.subtract(var3.multiply(var0.getV1A()).add(var4.multiply(var0.getV2A())));
      BigInteger var6 = var3.multiply(var0.getV1B()).add(var4.multiply(var0.getV2B())).negate();
      return new BigInteger[]{var5, var6};
   }

   public static ECPoint mapPoint(final ECEndomorphism var0, final ECPoint var1) {
      ECCurve var2 = var1.getCurve();
      EndoPreCompInfo var3 = (EndoPreCompInfo)var2.precompute(var1, "bc_endo", new PreCompCallback() {
         @Override
         public PreCompInfo precompute(PreCompInfo var1x) {
            EndoPreCompInfo var2x = var1x instanceof EndoPreCompInfo ? (EndoPreCompInfo)var1x : null;
            if (this.checkExisting(var2x, var0)) {
               return var2x;
            } else {
               ECPoint var3x = var0.getPointMap().map(var1);
               EndoPreCompInfo var4 = new EndoPreCompInfo();
               var4.setEndomorphism(var0);
               var4.setMappedPoint(var3x);
               return var4;
            }
         }

         private boolean checkExisting(EndoPreCompInfo var1x, ECEndomorphism var2x) {
            return null != var1x && var1x.getEndomorphism() == var2x && var1x.getMappedPoint() != null;
         }
      });
      return var3.getMappedPoint();
   }

   private static BigInteger calculateB(BigInteger var0, BigInteger var1, int var2) {
      boolean var3 = var1.signum() < 0;
      BigInteger var4 = var0.multiply(var1.abs());
      boolean var5 = var4.testBit(var2 - 1);
      var4 = var4.shiftRight(var2);
      if (var5) {
         var4 = var4.add(ECConstants.ONE);
      }

      return var3 ? var4.negate() : var4;
   }
}
