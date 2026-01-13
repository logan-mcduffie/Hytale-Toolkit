package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.encoders.Hex;

public class SecP192K1Curve extends ECCurve.AbstractFp {
   public static final BigInteger q = SecP192K1FieldElement.Q;
   private static final int SECP192K1_DEFAULT_COORDS = 2;
   private static final ECFieldElement[] SECP192K1_AFFINE_ZS = new ECFieldElement[]{new SecP192K1FieldElement(ECConstants.ONE)};
   protected SecP192K1Point infinity = new SecP192K1Point(this, null, null);

   public SecP192K1Curve() {
      super(q);
      this.a = this.fromBigInteger(ECConstants.ZERO);
      this.b = this.fromBigInteger(BigInteger.valueOf(3L));
      this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFE26F2FC170F69466A74DEFD8D"));
      this.cofactor = BigInteger.valueOf(1L);
      this.coord = 2;
   }

   @Override
   protected ECCurve cloneCurve() {
      return new SecP192K1Curve();
   }

   @Override
   public boolean supportsCoordinateSystem(int var1) {
      switch (var1) {
         case 2:
            return true;
         default:
            return false;
      }
   }

   @Override
   public BigInteger getQ() {
      return q;
   }

   @Override
   public int getFieldSize() {
      return q.bitLength();
   }

   @Override
   public ECFieldElement fromBigInteger(BigInteger var1) {
      return new SecP192K1FieldElement(var1);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
      return new SecP192K1Point(this, var1, var2);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
      return new SecP192K1Point(this, var1, var2, var3);
   }

   @Override
   public ECPoint getInfinity() {
      return this.infinity;
   }

   @Override
   public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
      final int[] var4 = new int[var3 * 6 * 2];
      int var5 = 0;

      for (int var6 = 0; var6 < var3; var6++) {
         ECPoint var7 = var1[var2 + var6];
         Nat192.copy(((SecP192K1FieldElement)var7.getRawXCoord()).x, 0, var4, var5);
         var5 += 6;
         Nat192.copy(((SecP192K1FieldElement)var7.getRawYCoord()).x, 0, var4, var5);
         var5 += 6;
      }

      return new AbstractECLookupTable() {
         @Override
         public int getSize() {
            return var3;
         }

         @Override
         public ECPoint lookup(int var1) {
            int[] var2x = Nat192.create();
            int[] var3x = Nat192.create();
            byte var4x = 0;

            for (int var5x = 0; var5x < var3; var5x++) {
               int var6 = (var5x ^ var1) - 1 >> 31;

               for (int var7 = 0; var7 < 6; var7++) {
                  var2x[var7] ^= var4[var4x + var7] & var6;
                  var3x[var7] ^= var4[var4x + 6 + var7] & var6;
               }

               var4x += 12;
            }

            return this.createPoint(var2x, var3x);
         }

         @Override
         public ECPoint lookupVar(int var1) {
            int[] var2x = Nat192.create();
            int[] var3x = Nat192.create();
            int var4x = var1 * 6 * 2;

            for (int var5x = 0; var5x < 6; var5x++) {
               var2x[var5x] = var4[var4x + var5x];
               var3x[var5x] = var4[var4x + 6 + var5x];
            }

            return this.createPoint(var2x, var3x);
         }

         private ECPoint createPoint(int[] var1, int[] var2x) {
            return SecP192K1Curve.this.createRawPoint(new SecP192K1FieldElement(var1), new SecP192K1FieldElement(var2x), SecP192K1Curve.SECP192K1_AFFINE_ZS);
         }
      };
   }

   @Override
   public ECFieldElement randomFieldElement(SecureRandom var1) {
      int[] var2 = Nat192.create();
      SecP192K1Field.random(var1, var2);
      return new SecP192K1FieldElement(var2);
   }

   @Override
   public ECFieldElement randomFieldElementMult(SecureRandom var1) {
      int[] var2 = Nat192.create();
      SecP192K1Field.randomMult(var1, var2);
      return new SecP192K1FieldElement(var2);
   }
}
