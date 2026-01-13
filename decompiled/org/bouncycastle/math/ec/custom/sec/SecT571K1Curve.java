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
import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.util.encoders.Hex;

public class SecT571K1Curve extends ECCurve.AbstractF2m {
   private static final int SECT571K1_DEFAULT_COORDS = 6;
   private static final ECFieldElement[] SECT571K1_AFFINE_ZS = new ECFieldElement[]{new SecT571FieldElement(ECConstants.ONE)};
   protected SecT571K1Point infinity = new SecT571K1Point(this, null, null);

   public SecT571K1Curve() {
      super(571, 2, 5, 10);
      this.a = this.fromBigInteger(BigInteger.valueOf(0L));
      this.b = this.fromBigInteger(BigInteger.valueOf(1L));
      this.order = new BigInteger(
         1,
         Hex.decodeStrict(
            "020000000000000000000000000000000000000000000000000000000000000000000000131850E1F19A63E4B391A8DB917F4138B630D84BE5D639381E91DEB45CFE778F637C1001"
         )
      );
      this.cofactor = BigInteger.valueOf(4L);
      this.coord = 6;
   }

   @Override
   protected ECCurve cloneCurve() {
      return new SecT571K1Curve();
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
      return 571;
   }

   @Override
   public ECFieldElement fromBigInteger(BigInteger var1) {
      return new SecT571FieldElement(var1);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
      return new SecT571K1Point(this, var1, var2);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
      return new SecT571K1Point(this, var1, var2, var3);
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
      return 571;
   }

   public boolean isTrinomial() {
      return false;
   }

   public int getK1() {
      return 2;
   }

   public int getK2() {
      return 5;
   }

   public int getK3() {
      return 10;
   }

   @Override
   public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
      final long[] var4 = new long[var3 * 9 * 2];
      int var5 = 0;

      for (int var6 = 0; var6 < var3; var6++) {
         ECPoint var7 = var1[var2 + var6];
         Nat576.copy64(((SecT571FieldElement)var7.getRawXCoord()).x, 0, var4, var5);
         var5 += 9;
         Nat576.copy64(((SecT571FieldElement)var7.getRawYCoord()).x, 0, var4, var5);
         var5 += 9;
      }

      return new AbstractECLookupTable() {
         @Override
         public int getSize() {
            return var3;
         }

         @Override
         public ECPoint lookup(int var1) {
            long[] var2x = Nat576.create64();
            long[] var3x = Nat576.create64();
            byte var4x = 0;

            for (int var5x = 0; var5x < var3; var5x++) {
               long var6 = (var5x ^ var1) - 1 >> 31;

               for (int var8 = 0; var8 < 9; var8++) {
                  var2x[var8] ^= var4[var4x + var8] & var6;
                  var3x[var8] ^= var4[var4x + 9 + var8] & var6;
               }

               var4x += 18;
            }

            return this.createPoint(var2x, var3x);
         }

         @Override
         public ECPoint lookupVar(int var1) {
            long[] var2x = Nat576.create64();
            long[] var3x = Nat576.create64();
            int var4x = var1 * 9 * 2;

            for (int var5x = 0; var5x < 9; var5x++) {
               var2x[var5x] = var4[var4x + var5x];
               var3x[var5x] = var4[var4x + 9 + var5x];
            }

            return this.createPoint(var2x, var3x);
         }

         private ECPoint createPoint(long[] var1, long[] var2x) {
            return SecT571K1Curve.this.createRawPoint(new SecT571FieldElement(var1), new SecT571FieldElement(var2x), SecT571K1Curve.SECT571K1_AFFINE_ZS);
         }
      };
   }
}
