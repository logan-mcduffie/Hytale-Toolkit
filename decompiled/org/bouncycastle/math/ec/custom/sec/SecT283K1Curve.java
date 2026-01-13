package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.WTauNafMultiplier;
import org.bouncycastle.math.raw.Nat320;
import org.bouncycastle.util.encoders.Hex;

public class SecT283K1Curve extends ECCurve.AbstractF2m {
   private static final int SECT283K1_DEFAULT_COORDS = 6;
   private static final ECFieldElement[] SECT283K1_AFFINE_ZS = new ECFieldElement[]{new SecT283FieldElement(ECConstants.ONE)};
   protected SecT283K1Point infinity = new SecT283K1Point(this, null, null);

   public SecT283K1Curve() {
      super(283, 5, 7, 12);
      this.a = this.fromBigInteger(BigInteger.valueOf(0L));
      this.b = this.fromBigInteger(BigInteger.valueOf(1L));
      this.order = new BigInteger(1, Hex.decodeStrict("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE9AE2ED07577265DFF7F94451E061E163C61"));
      this.cofactor = BigInteger.valueOf(4L);
      this.coord = 6;
   }

   @Override
   protected ECCurve cloneCurve() {
      return new SecT283K1Curve();
   }

   @Override
   public boolean supportsCoordinateSystem(int var1) {
      switch (var1) {
         case 6:
            return true;
         default:
            return false;
      }
   }

   @Override
   protected ECMultiplier createDefaultMultiplier() {
      return new WTauNafMultiplier();
   }

   @Override
   public int getFieldSize() {
      return 283;
   }

   @Override
   public ECFieldElement fromBigInteger(BigInteger var1) {
      return new SecT283FieldElement(var1);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
      return new SecT283K1Point(this, var1, var2);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
      return new SecT283K1Point(this, var1, var2, var3);
   }

   @Override
   public ECPoint getInfinity() {
      return this.infinity;
   }

   @Override
   public boolean isKoblitz() {
      return true;
   }

   public int getM() {
      return 283;
   }

   public boolean isTrinomial() {
      return false;
   }

   public int getK1() {
      return 5;
   }

   public int getK2() {
      return 7;
   }

   public int getK3() {
      return 12;
   }

   @Override
   public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
      final long[] var4 = new long[var3 * 5 * 2];
      int var5 = 0;

      for (int var6 = 0; var6 < var3; var6++) {
         ECPoint var7 = var1[var2 + var6];
         Nat320.copy64(((SecT283FieldElement)var7.getRawXCoord()).x, 0, var4, var5);
         var5 += 5;
         Nat320.copy64(((SecT283FieldElement)var7.getRawYCoord()).x, 0, var4, var5);
         var5 += 5;
      }

      return new AbstractECLookupTable() {
         @Override
         public int getSize() {
            return var3;
         }

         @Override
         public ECPoint lookup(int var1) {
            long[] var2x = Nat320.create64();
            long[] var3x = Nat320.create64();
            byte var4x = 0;

            for (int var5x = 0; var5x < var3; var5x++) {
               long var6 = (var5x ^ var1) - 1 >> 31;

               for (int var8 = 0; var8 < 5; var8++) {
                  var2x[var8] ^= var4[var4x + var8] & var6;
                  var3x[var8] ^= var4[var4x + 5 + var8] & var6;
               }

               var4x += 10;
            }

            return this.createPoint(var2x, var3x);
         }

         @Override
         public ECPoint lookupVar(int var1) {
            long[] var2x = Nat320.create64();
            long[] var3x = Nat320.create64();
            int var4x = var1 * 5 * 2;

            for (int var5x = 0; var5x < 5; var5x++) {
               var2x[var5x] = var4[var4x + var5x];
               var3x[var5x] = var4[var4x + 5 + var5x];
            }

            return this.createPoint(var2x, var3x);
         }

         private ECPoint createPoint(long[] var1, long[] var2x) {
            return SecT283K1Curve.this.createRawPoint(new SecT283FieldElement(var1), new SecT283FieldElement(var2x), SecT283K1Curve.SECT283K1_AFFINE_ZS);
         }
      };
   }
}
