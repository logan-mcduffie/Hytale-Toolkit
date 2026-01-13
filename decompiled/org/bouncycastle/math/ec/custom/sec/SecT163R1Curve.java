package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.encoders.Hex;

public class SecT163R1Curve extends ECCurve.AbstractF2m {
   private static final int SECT163R1_DEFAULT_COORDS = 6;
   private static final ECFieldElement[] SECT163R1_AFFINE_ZS = new ECFieldElement[]{new SecT163FieldElement(ECConstants.ONE)};
   protected SecT163R1Point infinity = new SecT163R1Point(this, null, null);

   public SecT163R1Curve() {
      super(163, 3, 6, 7);
      this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("07B6882CAAEFA84F9554FF8428BD88E246D2782AE2")));
      this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("0713612DCDDCB40AAB946BDA29CA91F73AF958AFD9")));
      this.order = new BigInteger(1, Hex.decodeStrict("03FFFFFFFFFFFFFFFFFFFF48AAB689C29CA710279B"));
      this.cofactor = BigInteger.valueOf(2L);
      this.coord = 6;
   }

   @Override
   protected ECCurve cloneCurve() {
      return new SecT163R1Curve();
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
      return 163;
   }

   @Override
   public ECFieldElement fromBigInteger(BigInteger var1) {
      return new SecT163FieldElement(var1);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
      return new SecT163R1Point(this, var1, var2);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
      return new SecT163R1Point(this, var1, var2, var3);
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
      return 163;
   }

   public boolean isTrinomial() {
      return false;
   }

   public int getK1() {
      return 3;
   }

   public int getK2() {
      return 6;
   }

   public int getK3() {
      return 7;
   }

   @Override
   public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
      final long[] var4 = new long[var3 * 3 * 2];
      int var5 = 0;

      for (int var6 = 0; var6 < var3; var6++) {
         ECPoint var7 = var1[var2 + var6];
         Nat192.copy64(((SecT163FieldElement)var7.getRawXCoord()).x, 0, var4, var5);
         var5 += 3;
         Nat192.copy64(((SecT163FieldElement)var7.getRawYCoord()).x, 0, var4, var5);
         var5 += 3;
      }

      return new AbstractECLookupTable() {
         @Override
         public int getSize() {
            return var3;
         }

         @Override
         public ECPoint lookup(int var1) {
            long[] var2x = Nat192.create64();
            long[] var3x = Nat192.create64();
            byte var4x = 0;

            for (int var5x = 0; var5x < var3; var5x++) {
               long var6 = (var5x ^ var1) - 1 >> 31;

               for (int var8 = 0; var8 < 3; var8++) {
                  var2x[var8] ^= var4[var4x + var8] & var6;
                  var3x[var8] ^= var4[var4x + 3 + var8] & var6;
               }

               var4x += 6;
            }

            return this.createPoint(var2x, var3x);
         }

         @Override
         public ECPoint lookupVar(int var1) {
            long[] var2x = Nat192.create64();
            long[] var3x = Nat192.create64();
            int var4x = var1 * 3 * 2;

            for (int var5x = 0; var5x < 3; var5x++) {
               var2x[var5x] = var4[var4x + var5x];
               var3x[var5x] = var4[var4x + 3 + var5x];
            }

            return this.createPoint(var2x, var3x);
         }

         private ECPoint createPoint(long[] var1, long[] var2x) {
            return SecT163R1Curve.this.createRawPoint(new SecT163FieldElement(var1), new SecT163FieldElement(var2x), SecT163R1Curve.SECT163R1_AFFINE_ZS);
         }
      };
   }
}
