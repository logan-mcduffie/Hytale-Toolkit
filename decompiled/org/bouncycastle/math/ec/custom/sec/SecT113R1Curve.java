package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.util.encoders.Hex;

public class SecT113R1Curve extends ECCurve.AbstractF2m {
   private static final int SECT113R1_DEFAULT_COORDS = 6;
   private static final ECFieldElement[] SECT113R1_AFFINE_ZS = new ECFieldElement[]{new SecT113FieldElement(ECConstants.ONE)};
   protected SecT113R1Point infinity = new SecT113R1Point(this, null, null);

   public SecT113R1Curve() {
      super(113, 9, 0, 0);
      this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("003088250CA6E7C7FE649CE85820F7")));
      this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("00E8BEE4D3E2260744188BE0E9C723")));
      this.order = new BigInteger(1, Hex.decodeStrict("0100000000000000D9CCEC8A39E56F"));
      this.cofactor = BigInteger.valueOf(2L);
      this.coord = 6;
   }

   @Override
   protected ECCurve cloneCurve() {
      return new SecT113R1Curve();
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
   public int getFieldSize() {
      return 113;
   }

   @Override
   public ECFieldElement fromBigInteger(BigInteger var1) {
      return new SecT113FieldElement(var1);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
      return new SecT113R1Point(this, var1, var2);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
      return new SecT113R1Point(this, var1, var2, var3);
   }

   @Override
   public ECPoint getInfinity() {
      return this.infinity;
   }

   @Override
   public boolean isKoblitz() {
      return false;
   }

   public int getM() {
      return 113;
   }

   public boolean isTrinomial() {
      return true;
   }

   public int getK1() {
      return 9;
   }

   public int getK2() {
      return 0;
   }

   public int getK3() {
      return 0;
   }

   @Override
   public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
      final long[] var4 = new long[var3 * 2 * 2];
      int var5 = 0;

      for (int var6 = 0; var6 < var3; var6++) {
         ECPoint var7 = var1[var2 + var6];
         Nat128.copy64(((SecT113FieldElement)var7.getRawXCoord()).x, 0, var4, var5);
         var5 += 2;
         Nat128.copy64(((SecT113FieldElement)var7.getRawYCoord()).x, 0, var4, var5);
         var5 += 2;
      }

      return new AbstractECLookupTable() {
         @Override
         public int getSize() {
            return var3;
         }

         @Override
         public ECPoint lookup(int var1) {
            long[] var2x = Nat128.create64();
            long[] var3x = Nat128.create64();
            byte var4x = 0;

            for (int var5x = 0; var5x < var3; var5x++) {
               long var6 = (var5x ^ var1) - 1 >> 31;

               for (int var8 = 0; var8 < 2; var8++) {
                  var2x[var8] ^= var4[var4x + var8] & var6;
                  var3x[var8] ^= var4[var4x + 2 + var8] & var6;
               }

               var4x += 4;
            }

            return this.createPoint(var2x, var3x);
         }

         @Override
         public ECPoint lookupVar(int var1) {
            long[] var2x = Nat128.create64();
            long[] var3x = Nat128.create64();
            int var4x = var1 * 2 * 2;

            for (int var5x = 0; var5x < 2; var5x++) {
               var2x[var5x] = var4[var4x + var5x];
               var3x[var5x] = var4[var4x + 2 + var5x];
            }

            return this.createPoint(var2x, var3x);
         }

         private ECPoint createPoint(long[] var1, long[] var2x) {
            return SecT113R1Curve.this.createRawPoint(new SecT113FieldElement(var1), new SecT113FieldElement(var2x), SecT113R1Curve.SECT113R1_AFFINE_ZS);
         }
      };
   }
}
