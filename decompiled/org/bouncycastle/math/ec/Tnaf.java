package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

class Tnaf {
   private static final BigInteger MINUS_ONE = ECConstants.ONE.negate();
   private static final BigInteger MINUS_TWO = ECConstants.TWO.negate();
   private static final BigInteger MINUS_THREE = ECConstants.THREE.negate();
   public static final byte WIDTH = 4;
   public static final ZTauElement[] alpha0 = new ZTauElement[]{
      null,
      new ZTauElement(ECConstants.ONE, ECConstants.ZERO),
      null,
      new ZTauElement(MINUS_THREE, MINUS_ONE),
      null,
      new ZTauElement(MINUS_ONE, MINUS_ONE),
      null,
      new ZTauElement(ECConstants.ONE, MINUS_ONE),
      null,
      new ZTauElement(MINUS_ONE, ECConstants.ONE),
      null,
      new ZTauElement(ECConstants.ONE, ECConstants.ONE),
      null,
      new ZTauElement(ECConstants.THREE, ECConstants.ONE),
      null,
      new ZTauElement(MINUS_ONE, ECConstants.ZERO)
   };
   public static final byte[][] alpha0Tnaf = new byte[][]{null, {1}, null, {-1, 0, 1}, null, {1, 0, 1}, null, {-1, 0, 0, 1}};
   public static final ZTauElement[] alpha1 = new ZTauElement[]{
      null,
      new ZTauElement(ECConstants.ONE, ECConstants.ZERO),
      null,
      new ZTauElement(MINUS_THREE, ECConstants.ONE),
      null,
      new ZTauElement(MINUS_ONE, ECConstants.ONE),
      null,
      new ZTauElement(ECConstants.ONE, ECConstants.ONE),
      null,
      new ZTauElement(MINUS_ONE, MINUS_ONE),
      null,
      new ZTauElement(ECConstants.ONE, MINUS_ONE),
      null,
      new ZTauElement(ECConstants.THREE, MINUS_ONE),
      null,
      new ZTauElement(MINUS_ONE, ECConstants.ZERO)
   };
   public static final byte[][] alpha1Tnaf = new byte[][]{null, {1}, null, {-1, 0, 1}, null, {1, 0, 1}, null, {-1, 0, 0, -1}};

   public static BigInteger norm(byte var0, ZTauElement var1) {
      BigInteger var2 = var1.u.multiply(var1.u);
      if (var0 == 1) {
         return var1.v.shiftLeft(1).add(var1.u).multiply(var1.v).add(var2);
      } else if (var0 == -1) {
         return var1.v.shiftLeft(1).subtract(var1.u).multiply(var1.v).add(var2);
      } else {
         throw new IllegalArgumentException("mu must be 1 or -1");
      }
   }

   public static SimpleBigDecimal norm(byte var0, SimpleBigDecimal var1, SimpleBigDecimal var2) {
      SimpleBigDecimal var4 = var1.multiply(var1);
      SimpleBigDecimal var5 = var1.multiply(var2);
      SimpleBigDecimal var6 = var2.multiply(var2).shiftLeft(1);
      SimpleBigDecimal var3;
      if (var0 == 1) {
         var3 = var4.add(var5).add(var6);
      } else {
         if (var0 != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
         }

         var3 = var4.subtract(var5).add(var6);
      }

      return var3;
   }

   public static ZTauElement round(SimpleBigDecimal var0, SimpleBigDecimal var1, byte var2) {
      int var3 = var0.getScale();
      if (var1.getScale() != var3) {
         throw new IllegalArgumentException("lambda0 and lambda1 do not have same scale");
      } else if (var2 != 1 && var2 != -1) {
         throw new IllegalArgumentException("mu must be 1 or -1");
      } else {
         BigInteger var4 = var0.round();
         BigInteger var5 = var1.round();
         SimpleBigDecimal var6 = var0.subtract(var4);
         SimpleBigDecimal var7 = var1.subtract(var5);
         SimpleBigDecimal var8 = var6.add(var6);
         if (var2 == 1) {
            var8 = var8.add(var7);
         } else {
            var8 = var8.subtract(var7);
         }

         SimpleBigDecimal var9 = var7.add(var7).add(var7);
         SimpleBigDecimal var10 = var9.add(var7);
         SimpleBigDecimal var11;
         SimpleBigDecimal var12;
         if (var2 == 1) {
            var11 = var6.subtract(var9);
            var12 = var6.add(var10);
         } else {
            var11 = var6.add(var9);
            var12 = var6.subtract(var10);
         }

         byte var13 = 0;
         byte var14 = 0;
         if (var8.compareTo(ECConstants.ONE) >= 0) {
            if (var11.compareTo(MINUS_ONE) < 0) {
               var14 = var2;
            } else {
               var13 = 1;
            }
         } else if (var12.compareTo(ECConstants.TWO) >= 0) {
            var14 = var2;
         }

         if (var8.compareTo(MINUS_ONE) < 0) {
            if (var11.compareTo(ECConstants.ONE) >= 0) {
               var14 = (byte)(-var2);
            } else {
               var13 = -1;
            }
         } else if (var12.compareTo(MINUS_TWO) < 0) {
            var14 = (byte)(-var2);
         }

         BigInteger var15 = var4.add(BigInteger.valueOf(var13));
         BigInteger var16 = var5.add(BigInteger.valueOf(var14));
         return new ZTauElement(var15, var16);
      }
   }

   public static SimpleBigDecimal approximateDivisionByN(BigInteger var0, BigInteger var1, BigInteger var2, byte var3, int var4, int var5) {
      int var6 = (var4 + 5) / 2 + var5;
      BigInteger var7 = var0.shiftRight(var4 - var6 - 2 + var3);
      BigInteger var8 = var1.multiply(var7);
      BigInteger var9 = var8.shiftRight(var4);
      BigInteger var10 = var2.multiply(var9);
      BigInteger var11 = var8.add(var10);
      BigInteger var12 = var11.shiftRight(var6 - var5);
      if (var11.testBit(var6 - var5 - 1)) {
         var12 = var12.add(ECConstants.ONE);
      }

      return new SimpleBigDecimal(var12, var5);
   }

   public static byte[] tauAdicNaf(byte var0, ZTauElement var1) {
      if (var0 != 1 && var0 != -1) {
         throw new IllegalArgumentException("mu must be 1 or -1");
      } else {
         BigInteger var2 = norm(var0, var1);
         int var3 = var2.bitLength();
         int var4 = var3 > 30 ? var3 + 4 : 34;
         byte[] var5 = new byte[var4];
         int var6 = 0;
         int var7 = 0;
         BigInteger var8 = var1.u;

         for (BigInteger var9 = var1.v; !var8.equals(ECConstants.ZERO) || !var9.equals(ECConstants.ZERO); var6++) {
            if (var8.testBit(0)) {
               var5[var6] = (byte)ECConstants.TWO.subtract(var8.subtract(var9.shiftLeft(1)).mod(ECConstants.FOUR)).intValue();
               if (var5[var6] == 1) {
                  var8 = var8.clearBit(0);
               } else {
                  var8 = var8.add(ECConstants.ONE);
               }

               var7 = var6;
            } else {
               var5[var6] = 0;
            }

            BigInteger var10 = var8;
            BigInteger var11 = var8.shiftRight(1);
            if (var0 == 1) {
               var8 = var9.add(var11);
            } else {
               var8 = var9.subtract(var11);
            }

            var9 = var10.shiftRight(1).negate();
         }

         byte[] var13 = new byte[++var7];
         System.arraycopy(var5, 0, var13, 0, var7);
         return var13;
      }
   }

   public static ECPoint.AbstractF2m tau(ECPoint.AbstractF2m var0) {
      return var0.tau();
   }

   public static byte getMu(ECCurve.AbstractF2m var0) {
      if (!var0.isKoblitz()) {
         throw new IllegalArgumentException("No Koblitz curve (ABC), TNAF multiplication not possible");
      } else {
         return (byte)(var0.getA().isZero() ? -1 : 1);
      }
   }

   public static byte getMu(ECFieldElement var0) {
      return (byte)(var0.isZero() ? -1 : 1);
   }

   public static byte getMu(int var0) {
      return (byte)(var0 == 0 ? -1 : 1);
   }

   public static BigInteger[] getLucas(byte var0, int var1, boolean var2) {
      if (var0 != 1 && var0 != -1) {
         throw new IllegalArgumentException("mu must be 1 or -1");
      } else {
         BigInteger var3;
         BigInteger var4;
         if (var2) {
            var3 = ECConstants.TWO;
            var4 = BigInteger.valueOf(var0);
         } else {
            var3 = ECConstants.ZERO;
            var4 = ECConstants.ONE;
         }

         for (int var6 = 1; var6 < var1; var6++) {
            BigInteger var7 = var4;
            if (var0 < 0) {
               var7 = var4.negate();
            }

            BigInteger var5 = var7.subtract(var3.shiftLeft(1));
            var3 = var4;
            var4 = var5;
         }

         return new BigInteger[]{var3, var4};
      }
   }

   public static BigInteger getTw(byte var0, int var1) {
      if (var1 == 4) {
         return var0 == 1 ? BigInteger.valueOf(6L) : BigInteger.valueOf(10L);
      } else {
         BigInteger[] var2 = getLucas(var0, var1, false);
         BigInteger var3 = ECConstants.ZERO.setBit(var1);
         BigInteger var4 = var2[1].modInverse(var3);
         return var2[0].shiftLeft(1).multiply(var4).mod(var3);
      }
   }

   public static BigInteger[] getSi(ECCurve.AbstractF2m var0) {
      if (!var0.isKoblitz()) {
         throw new IllegalArgumentException("si is defined for Koblitz curves only");
      } else {
         return getSi(var0.getFieldSize(), var0.getA().toBigInteger().intValue(), var0.getCofactor());
      }
   }

   public static BigInteger[] getSi(int var0, int var1, BigInteger var2) {
      byte var3 = getMu(var1);
      int var4 = getShiftsForCofactor(var2);
      int var5 = var0 + 3 - var1;
      BigInteger[] var6 = getLucas(var3, var5, false);
      if (var3 == 1) {
         var6[0] = var6[0].negate();
         var6[1] = var6[1].negate();
      }

      BigInteger var7 = ECConstants.ONE.add(var6[1]).shiftRight(var4);
      BigInteger var8 = ECConstants.ONE.add(var6[0]).shiftRight(var4).negate();
      return new BigInteger[]{var7, var8};
   }

   protected static int getShiftsForCofactor(BigInteger var0) {
      if (var0 != null) {
         if (var0.equals(ECConstants.TWO)) {
            return 1;
         }

         if (var0.equals(ECConstants.FOUR)) {
            return 2;
         }
      }

      throw new IllegalArgumentException("h (Cofactor) must be 2 or 4");
   }

   public static ZTauElement partModReduction(ECCurve.AbstractF2m var0, BigInteger var1, byte var2, byte var3, byte var4) {
      int var5 = var0.getFieldSize();
      BigInteger[] var6 = var0.getSi();
      BigInteger var7;
      if (var3 == 1) {
         var7 = var6[0].add(var6[1]);
      } else {
         var7 = var6[0].subtract(var6[1]);
      }

      BigInteger var8;
      if (var0.isKoblitz()) {
         var8 = ECConstants.ONE.shiftLeft(var5).add(ECConstants.ONE).subtract(var0.getOrder().multiply(var0.getCofactor()));
      } else {
         BigInteger[] var9 = getLucas(var3, var5, true);
         var8 = var9[1];
      }

      SimpleBigDecimal var14 = approximateDivisionByN(var1, var6[0], var8, var2, var5, var4);
      SimpleBigDecimal var10 = approximateDivisionByN(var1, var6[1], var8, var2, var5, var4);
      ZTauElement var11 = round(var14, var10, var3);
      BigInteger var12 = var1.subtract(var7.multiply(var11.u)).subtract(var6[1].multiply(var11.v).shiftLeft(1));
      BigInteger var13 = var6[1].multiply(var11.u).subtract(var6[0].multiply(var11.v));
      return new ZTauElement(var12, var13);
   }

   public static ECPoint.AbstractF2m multiplyRTnaf(ECPoint.AbstractF2m var0, BigInteger var1) {
      ECCurve.AbstractF2m var2 = (ECCurve.AbstractF2m)var0.getCurve();
      int var3 = var2.getA().toBigInteger().intValue();
      byte var4 = getMu(var3);
      ZTauElement var5 = partModReduction(var2, var1, (byte)var3, var4, (byte)10);
      return multiplyTnaf(var0, var5);
   }

   public static ECPoint.AbstractF2m multiplyTnaf(ECPoint.AbstractF2m var0, ZTauElement var1) {
      ECCurve.AbstractF2m var2 = (ECCurve.AbstractF2m)var0.getCurve();
      ECPoint.AbstractF2m var3 = (ECPoint.AbstractF2m)var0.negate();
      byte var4 = getMu(var2.getA());
      byte[] var5 = tauAdicNaf(var4, var1);
      return multiplyFromTnaf(var0, var3, var5);
   }

   public static ECPoint.AbstractF2m multiplyFromTnaf(ECPoint.AbstractF2m var0, ECPoint.AbstractF2m var1, byte[] var2) {
      ECCurve var3 = var0.getCurve();
      ECPoint.AbstractF2m var4 = (ECPoint.AbstractF2m)var3.getInfinity();
      int var5 = 0;

      for (int var6 = var2.length - 1; var6 >= 0; var6--) {
         var5++;
         byte var7 = var2[var6];
         if (var7 != 0) {
            var4 = var4.tauPow(var5);
            var5 = 0;
            ECPoint.AbstractF2m var8 = var7 > 0 ? var0 : var1;
            var4 = (ECPoint.AbstractF2m)var4.add(var8);
         }
      }

      if (var5 > 0) {
         var4 = var4.tauPow(var5);
      }

      return var4;
   }

   public static byte[] tauAdicWNaf(byte var0, ZTauElement var1, int var2, int var3, ZTauElement[] var4) {
      if (var0 != 1 && var0 != -1) {
         throw new IllegalArgumentException("mu must be 1 or -1");
      } else {
         BigInteger var5 = norm(var0, var1);
         int var6 = var5.bitLength();
         int var7 = var6 > 30 ? var6 + 4 + var2 : 34 + var2;
         byte[] var8 = new byte[var7];
         int var9 = 1 << var2;
         int var10 = var9 - 1;
         int var11 = 32 - var2;
         BigInteger var12 = var1.u;
         BigInteger var13 = var1.v;
         int var14 = 0;

         while (var12.bitLength() > 62 || var13.bitLength() > 62) {
            if (var12.testBit(0)) {
               int var15 = var12.intValue() + var13.intValue() * var3;
               int var16 = var15 & var10;
               var8[var14] = (byte)(var15 << var11 >> var11);
               var12 = var12.subtract(var4[var16].u);
               var13 = var13.subtract(var4[var16].v);
            }

            var14++;
            BigInteger var21 = var12.shiftRight(1);
            if (var0 == 1) {
               var12 = var13.add(var21);
            } else {
               var12 = var13.subtract(var21);
            }

            var13 = var21.negate();
         }

         long var22 = BigIntegers.longValueExact(var12);
         long var17 = BigIntegers.longValueExact(var13);

         while ((var22 | var17) != 0L) {
            if ((var22 & 1L) != 0L) {
               int var19 = (int)var22 + (int)var17 * var3;
               int var20 = var19 & var10;
               var8[var14] = (byte)(var19 << var11 >> var11);
               var22 -= var4[var20].u.intValue();
               var17 -= var4[var20].v.intValue();
            }

            var14++;
            long var23 = var22 >> 1;
            if (var0 == 1) {
               var22 = var17 + var23;
            } else {
               var22 = var17 - var23;
            }

            var17 = -var23;
         }

         return var8;
      }
   }

   public static ECPoint.AbstractF2m[] getPreComp(ECPoint.AbstractF2m var0, byte var1) {
      ECPoint.AbstractF2m var2 = (ECPoint.AbstractF2m)var0.negate();
      byte[][] var3 = var1 == 0 ? alpha0Tnaf : alpha1Tnaf;
      ECPoint.AbstractF2m[] var4 = new ECPoint.AbstractF2m[var3.length + 1 >>> 1];
      var4[0] = var0;
      int var5 = var3.length;

      for (byte var6 = 3; var6 < var5; var6 += 2) {
         var4[var6 >>> 1] = multiplyFromTnaf(var0, var2, var3[var6]);
      }

      var0.getCurve().normalizeAll(var4);
      return var4;
   }
}
