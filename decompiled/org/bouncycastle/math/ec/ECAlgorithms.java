package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.EndoUtil;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.raw.Nat;

public class ECAlgorithms {
   public static boolean isF2mCurve(ECCurve var0) {
      return isF2mField(var0.getField());
   }

   public static boolean isF2mField(FiniteField var0) {
      return var0.getDimension() > 1 && var0.getCharacteristic().equals(ECConstants.TWO) && var0 instanceof PolynomialExtensionField;
   }

   public static boolean isFpCurve(ECCurve var0) {
      return isFpField(var0.getField());
   }

   public static boolean isFpField(FiniteField var0) {
      return var0.getDimension() == 1;
   }

   public static ECPoint sumOfMultiplies(ECPoint[] var0, BigInteger[] var1) {
      if (var0 != null && var1 != null && var0.length == var1.length && var0.length >= 1) {
         int var2 = var0.length;
         switch (var2) {
            case 1:
               return var0[0].multiply(var1[0]);
            case 2:
               return sumOfTwoMultiplies(var0[0], var1[0], var0[1], var1[1]);
            default:
               ECPoint var3 = var0[0];
               ECCurve var4 = var3.getCurve();
               ECPoint[] var5 = new ECPoint[var2];
               var5[0] = var3;

               for (int var6 = 1; var6 < var2; var6++) {
                  var5[var6] = importPoint(var4, var0[var6]);
               }

               ECEndomorphism var7 = var4.getEndomorphism();
               return var7 instanceof GLVEndomorphism
                  ? implCheckResult(implSumOfMultipliesGLV(var5, var1, (GLVEndomorphism)var7))
                  : implCheckResult(implSumOfMultiplies(var5, var1));
         }
      } else {
         throw new IllegalArgumentException("point and scalar arrays should be non-null, and of equal, non-zero, length");
      }
   }

   public static ECPoint sumOfTwoMultiplies(ECPoint var0, BigInteger var1, ECPoint var2, BigInteger var3) {
      ECCurve var4 = var0.getCurve();
      var2 = importPoint(var4, var2);
      if (var4 instanceof ECCurve.AbstractF2m) {
         ECCurve.AbstractF2m var5 = (ECCurve.AbstractF2m)var4;
         if (var5.isKoblitz()) {
            return implCheckResult(var0.multiply(var1).add(var2.multiply(var3)));
         }
      }

      ECEndomorphism var7 = var4.getEndomorphism();
      return var7 instanceof GLVEndomorphism
         ? implCheckResult(implSumOfMultipliesGLV(new ECPoint[]{var0, var2}, new BigInteger[]{var1, var3}, (GLVEndomorphism)var7))
         : implCheckResult(implShamirsTrickWNaf(var0, var1, var2, var3));
   }

   public static ECPoint shamirsTrick(ECPoint var0, BigInteger var1, ECPoint var2, BigInteger var3) {
      ECCurve var4 = var0.getCurve();
      var2 = importPoint(var4, var2);
      return implCheckResult(implShamirsTrickJsf(var0, var1, var2, var3));
   }

   public static ECPoint importPoint(ECCurve var0, ECPoint var1) {
      ECCurve var2 = var1.getCurve();
      if (!var0.equals(var2)) {
         throw new IllegalArgumentException("Point must be on the same curve");
      } else {
         return var0.importPoint(var1);
      }
   }

   public static void montgomeryTrick(ECFieldElement[] var0, int var1, int var2) {
      montgomeryTrick(var0, var1, var2, null);
   }

   public static void montgomeryTrick(ECFieldElement[] var0, int var1, int var2, ECFieldElement var3) {
      ECFieldElement[] var4 = new ECFieldElement[var2];
      var4[0] = var0[var1];
      int var5 = 0;

      while (++var5 < var2) {
         var4[var5] = var4[var5 - 1].multiply(var0[var1 + var5]);
      }

      var5--;
      if (var3 != null) {
         var4[var5] = var4[var5].multiply(var3);
      }

      ECFieldElement var6 = var4[var5].invert();

      while (var5 > 0) {
         int var7 = var1 + var5--;
         ECFieldElement var8 = var0[var7];
         var0[var7] = var4[var5].multiply(var6);
         var6 = var6.multiply(var8);
      }

      var0[var1] = var6;
   }

   public static ECPoint referenceMultiply(ECPoint var0, BigInteger var1) {
      BigInteger var2 = var1.abs();
      ECPoint var3 = var0.getCurve().getInfinity();
      int var4 = var2.bitLength();
      if (var4 > 0) {
         if (var2.testBit(0)) {
            var3 = var0;
         }

         for (int var5 = 1; var5 < var4; var5++) {
            var0 = var0.twice();
            if (var2.testBit(var5)) {
               var3 = var3.add(var0);
            }
         }
      }

      return var1.signum() < 0 ? var3.negate() : var3;
   }

   public static ECPoint validatePoint(ECPoint var0) {
      if (!var0.isValid()) {
         throw new IllegalStateException("Invalid point");
      } else {
         return var0;
      }
   }

   public static ECPoint cleanPoint(ECCurve var0, ECPoint var1) {
      ECCurve var2 = var1.getCurve();
      if (!var0.equals(var2)) {
         throw new IllegalArgumentException("Point must be on the same curve");
      } else {
         return var0.decodePoint(var1.getEncoded(false));
      }
   }

   static ECPoint implCheckResult(ECPoint var0) {
      if (!var0.isValidPartial()) {
         throw new IllegalStateException("Invalid result");
      } else {
         return var0;
      }
   }

   static ECPoint implShamirsTrickJsf(ECPoint var0, BigInteger var1, ECPoint var2, BigInteger var3) {
      ECCurve var4 = var0.getCurve();
      ECPoint var5 = var4.getInfinity();
      ECPoint var6 = var0.add(var2);
      ECPoint var7 = var0.subtract(var2);
      ECPoint[] var8 = new ECPoint[]{var2, var7, var0, var6};
      var4.normalizeAll(var8);
      ECPoint[] var9 = new ECPoint[]{var8[3].negate(), var8[2].negate(), var8[1].negate(), var8[0].negate(), var5, var8[0], var8[1], var8[2], var8[3]};
      byte[] var10 = WNafUtil.generateJSF(var1, var3);
      ECPoint var11 = var5;
      int var12 = var10.length;

      while (--var12 >= 0) {
         byte var13 = var10[var12];
         int var14 = var13 << 24 >> 28;
         int var15 = var13 << 28 >> 28;
         int var16 = 4 + var14 * 3 + var15;
         var11 = var11.twicePlus(var9[var16]);
      }

      return var11;
   }

   static ECPoint implShamirsTrickWNaf(ECPoint var0, BigInteger var1, ECPoint var2, BigInteger var3) {
      boolean var4 = var1.signum() < 0;
      boolean var5 = var3.signum() < 0;
      BigInteger var6 = var1.abs();
      BigInteger var7 = var3.abs();
      int var8 = WNafUtil.getWindowSize(var6.bitLength(), 8);
      int var9 = WNafUtil.getWindowSize(var7.bitLength(), 8);
      WNafPreCompInfo var10 = WNafUtil.precompute(var0, var8, true);
      WNafPreCompInfo var11 = WNafUtil.precompute(var2, var9, true);
      ECCurve var12 = var0.getCurve();
      int var13 = FixedPointUtil.getCombSize(var12);
      if (!var4 && !var5 && var1.bitLength() <= var13 && var3.bitLength() <= var13 && var10.isPromoted() && var11.isPromoted()) {
         return implShamirsTrickFixedPoint(var0, var1, var2, var3);
      } else {
         int var20 = Math.min(8, var10.getWidth());
         var13 = Math.min(8, var11.getWidth());
         ECPoint[] var14 = var4 ? var10.getPreCompNeg() : var10.getPreComp();
         ECPoint[] var15 = var5 ? var11.getPreCompNeg() : var11.getPreComp();
         ECPoint[] var16 = var4 ? var10.getPreComp() : var10.getPreCompNeg();
         ECPoint[] var17 = var5 ? var11.getPreComp() : var11.getPreCompNeg();
         byte[] var18 = WNafUtil.generateWindowNaf(var20, var6);
         byte[] var19 = WNafUtil.generateWindowNaf(var13, var7);
         return implShamirsTrickWNaf(var14, var16, var18, var15, var17, var19);
      }
   }

   static ECPoint implShamirsTrickWNaf(ECEndomorphism var0, ECPoint var1, BigInteger var2, BigInteger var3) {
      boolean var4 = var2.signum() < 0;
      boolean var5 = var3.signum() < 0;
      var2 = var2.abs();
      var3 = var3.abs();
      int var6 = WNafUtil.getWindowSize(Math.max(var2.bitLength(), var3.bitLength()), 8);
      WNafPreCompInfo var7 = WNafUtil.precompute(var1, var6, true);
      ECPoint var8 = EndoUtil.mapPoint(var0, var1);
      WNafPreCompInfo var9 = WNafUtil.precomputeWithPointMap(var8, var0.getPointMap(), var7, true);
      int var10 = Math.min(8, var7.getWidth());
      int var11 = Math.min(8, var9.getWidth());
      ECPoint[] var12 = var4 ? var7.getPreCompNeg() : var7.getPreComp();
      ECPoint[] var13 = var5 ? var9.getPreCompNeg() : var9.getPreComp();
      ECPoint[] var14 = var4 ? var7.getPreComp() : var7.getPreCompNeg();
      ECPoint[] var15 = var5 ? var9.getPreComp() : var9.getPreCompNeg();
      byte[] var16 = WNafUtil.generateWindowNaf(var10, var2);
      byte[] var17 = WNafUtil.generateWindowNaf(var11, var3);
      return implShamirsTrickWNaf(var12, var14, var16, var13, var15, var17);
   }

   private static ECPoint implShamirsTrickWNaf(ECPoint[] var0, ECPoint[] var1, byte[] var2, ECPoint[] var3, ECPoint[] var4, byte[] var5) {
      int var6 = Math.max(var2.length, var5.length);
      ECCurve var7 = var0[0].getCurve();
      ECPoint var8 = var7.getInfinity();
      ECPoint var9 = var8;
      int var10 = 0;

      for (int var11 = var6 - 1; var11 >= 0; var11--) {
         byte var12 = var11 < var2.length ? var2[var11] : 0;
         byte var13 = var11 < var5.length ? var5[var11] : 0;
         if ((var12 | var13) == 0) {
            var10++;
         } else {
            ECPoint var14 = var8;
            if (var12 != 0) {
               int var15 = Math.abs((int)var12);
               ECPoint[] var16 = var12 < 0 ? var1 : var0;
               var14 = var8.add(var16[var15 >>> 1]);
            }

            if (var13 != 0) {
               int var17 = Math.abs((int)var13);
               ECPoint[] var18 = var13 < 0 ? var4 : var3;
               var14 = var14.add(var18[var17 >>> 1]);
            }

            if (var10 > 0) {
               var9 = var9.timesPow2(var10);
               var10 = 0;
            }

            var9 = var9.twicePlus(var14);
         }
      }

      if (var10 > 0) {
         var9 = var9.timesPow2(var10);
      }

      return var9;
   }

   static ECPoint implSumOfMultiplies(ECPoint[] var0, BigInteger[] var1) {
      int var2 = var0.length;
      boolean[] var3 = new boolean[var2];
      WNafPreCompInfo[] var4 = new WNafPreCompInfo[var2];
      byte[][] var5 = new byte[var2][];

      for (int var6 = 0; var6 < var2; var6++) {
         BigInteger var7 = var1[var6];
         var3[var6] = var7.signum() < 0;
         var7 = var7.abs();
         int var8 = WNafUtil.getWindowSize(var7.bitLength(), 8);
         WNafPreCompInfo var9 = WNafUtil.precompute(var0[var6], var8, true);
         int var10 = Math.min(8, var9.getWidth());
         var4[var6] = var9;
         var5[var6] = WNafUtil.generateWindowNaf(var10, var7);
      }

      return implSumOfMultiplies(var3, var4, var5);
   }

   static ECPoint implSumOfMultipliesGLV(ECPoint[] var0, BigInteger[] var1, GLVEndomorphism var2) {
      BigInteger var3 = var0[0].getCurve().getOrder();
      int var4 = var0.length;
      BigInteger[] var5 = new BigInteger[var4 << 1];
      int var6 = 0;

      for (int var7 = 0; var6 < var4; var6++) {
         BigInteger[] var8 = var2.decomposeScalar(var1[var6].mod(var3));
         var5[var7++] = var8[0];
         var5[var7++] = var8[1];
      }

      if (var2.hasEfficientPointMap()) {
         return implSumOfMultiplies(var2, var0, var5);
      } else {
         ECPoint[] var11 = new ECPoint[var4 << 1];
         int var13 = 0;

         for (int var14 = 0; var13 < var4; var13++) {
            ECPoint var9 = var0[var13];
            ECPoint var10 = EndoUtil.mapPoint(var2, var9);
            var11[var14++] = var9;
            var11[var14++] = var10;
         }

         return implSumOfMultiplies(var11, var5);
      }
   }

   static ECPoint implSumOfMultiplies(ECEndomorphism var0, ECPoint[] var1, BigInteger[] var2) {
      int var3 = var1.length;
      int var4 = var3 << 1;
      boolean[] var5 = new boolean[var4];
      WNafPreCompInfo[] var6 = new WNafPreCompInfo[var4];
      byte[][] var7 = new byte[var4][];
      ECPointMap var8 = var0.getPointMap();

      for (int var9 = 0; var9 < var3; var9++) {
         int var10 = var9 << 1;
         int var11 = var10 + 1;
         BigInteger var12 = var2[var10];
         var5[var10] = var12.signum() < 0;
         var12 = var12.abs();
         BigInteger var13 = var2[var11];
         var5[var11] = var13.signum() < 0;
         var13 = var13.abs();
         int var14 = WNafUtil.getWindowSize(Math.max(var12.bitLength(), var13.bitLength()), 8);
         ECPoint var15 = var1[var9];
         WNafPreCompInfo var16 = WNafUtil.precompute(var15, var14, true);
         ECPoint var17 = EndoUtil.mapPoint(var0, var15);
         WNafPreCompInfo var18 = WNafUtil.precomputeWithPointMap(var17, var8, var16, true);
         int var19 = Math.min(8, var16.getWidth());
         int var20 = Math.min(8, var18.getWidth());
         var6[var10] = var16;
         var6[var11] = var18;
         var7[var10] = WNafUtil.generateWindowNaf(var19, var12);
         var7[var11] = WNafUtil.generateWindowNaf(var20, var13);
      }

      return implSumOfMultiplies(var5, var6, var7);
   }

   private static ECPoint implSumOfMultiplies(boolean[] var0, WNafPreCompInfo[] var1, byte[][] var2) {
      int var3 = 0;
      int var4 = var2.length;

      for (int var5 = 0; var5 < var4; var5++) {
         var3 = Math.max(var3, var2[var5].length);
      }

      ECCurve var17 = var1[0].getPreComp()[0].getCurve();
      ECPoint var6 = var17.getInfinity();
      ECPoint var7 = var6;
      int var8 = 0;

      for (int var9 = var3 - 1; var9 >= 0; var9--) {
         ECPoint var10 = var6;

         for (int var11 = 0; var11 < var4; var11++) {
            byte[] var12 = var2[var11];
            byte var13 = var9 < var12.length ? var12[var9] : 0;
            if (var13 != 0) {
               int var14 = Math.abs((int)var13);
               WNafPreCompInfo var15 = var1[var11];
               ECPoint[] var16 = var13 < 0 == var0[var11] ? var15.getPreComp() : var15.getPreCompNeg();
               var10 = var10.add(var16[var14 >>> 1]);
            }
         }

         if (var10 == var6) {
            var8++;
         } else {
            if (var8 > 0) {
               var7 = var7.timesPow2(var8);
               var8 = 0;
            }

            var7 = var7.twicePlus(var10);
         }
      }

      if (var8 > 0) {
         var7 = var7.timesPow2(var8);
      }

      return var7;
   }

   private static ECPoint implShamirsTrickFixedPoint(ECPoint var0, BigInteger var1, ECPoint var2, BigInteger var3) {
      ECCurve var4 = var0.getCurve();
      int var5 = FixedPointUtil.getCombSize(var4);
      if (var1.bitLength() <= var5 && var3.bitLength() <= var5) {
         FixedPointPreCompInfo var6 = FixedPointUtil.precompute(var0);
         FixedPointPreCompInfo var7 = FixedPointUtil.precompute(var2);
         ECLookupTable var8 = var6.getLookupTable();
         ECLookupTable var9 = var7.getLookupTable();
         int var10 = var6.getWidth();
         int var11 = var7.getWidth();
         if (var10 != var11) {
            FixedPointCombMultiplier var12 = new FixedPointCombMultiplier();
            ECPoint var25 = var12.multiply(var0, var1);
            ECPoint var26 = var12.multiply(var2, var3);
            return var25.add(var26);
         } else {
            int var13 = (var5 + var10 - 1) / var10;
            ECPoint var14 = var4.getInfinity();
            int var15 = var13 * var10;
            int[] var16 = Nat.fromBigInteger(var15, var1);
            int[] var17 = Nat.fromBigInteger(var15, var3);
            int var18 = var15 - 1;

            for (int var19 = 0; var19 < var13; var19++) {
               int var20 = 0;
               int var21 = 0;

               for (int var22 = var18 - var19; var22 >= 0; var22 -= var13) {
                  int var23 = var16[var22 >>> 5] >>> (var22 & 31);
                  var20 ^= var23 >>> 1;
                  var20 <<= 1;
                  var20 ^= var23;
                  int var24 = var17[var22 >>> 5] >>> (var22 & 31);
                  var21 ^= var24 >>> 1;
                  var21 <<= 1;
                  var21 ^= var24;
               }

               ECPoint var31 = var8.lookupVar(var20);
               ECPoint var32 = var9.lookupVar(var21);
               ECPoint var33 = var31.add(var32);
               var14 = var14.twicePlus(var33);
            }

            return var14.add(var6.getOffset()).add(var7.getOffset());
         }
      } else {
         throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order");
      }
   }
}
