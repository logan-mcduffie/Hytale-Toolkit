package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.encoders.Hex;

public class SecP521R1Curve extends ECCurve.AbstractFp {
   public static final BigInteger q = SecP521R1FieldElement.Q;
   private static final int SECP521R1_DEFAULT_COORDS = 2;
   private static final ECFieldElement[] SECP521R1_AFFINE_ZS = new ECFieldElement[]{new SecP521R1FieldElement(ECConstants.ONE)};
   protected SecP521R1Point infinity = new SecP521R1Point(this, null, null);

   public SecP521R1Curve() {
      super(q);
      this.a = this.fromBigInteger(
         new BigInteger(
            1,
            Hex.decodeStrict(
               "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC"
            )
         )
      );
      this.b = this.fromBigInteger(
         new BigInteger(
            1,
            Hex.decodeStrict(
               "0051953EB9618E1C9A1F929A21A0B68540EEA2DA725B99B315F3B8B489918EF109E156193951EC7E937B1652C0BD3BB1BF073573DF883D2C34F1EF451FD46B503F00"
            )
         )
      );
      this.order = new BigInteger(
         1,
         Hex.decodeStrict(
            "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA51868783BF2F966B7FCC0148F709A5D03BB5C9B8899C47AEBB6FB71E91386409"
         )
      );
      this.cofactor = BigInteger.valueOf(1L);
      this.coord = 2;
   }

   @Override
   protected ECCurve cloneCurve() {
      return new SecP521R1Curve();
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
      return new SecP521R1FieldElement(var1);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
      return new SecP521R1Point(this, var1, var2);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
      return new SecP521R1Point(this, var1, var2, var3);
   }

   @Override
   public ECPoint getInfinity() {
      return this.infinity;
   }

   @Override
   public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
      final int[] var4 = new int[var3 * 17 * 2];
      int var5 = 0;

      for (int var6 = 0; var6 < var3; var6++) {
         ECPoint var7 = var1[var2 + var6];
         Nat.copy(17, ((SecP521R1FieldElement)var7.getRawXCoord()).x, 0, var4, var5);
         var5 += 17;
         Nat.copy(17, ((SecP521R1FieldElement)var7.getRawYCoord()).x, 0, var4, var5);
         var5 += 17;
      }

      return new AbstractECLookupTable() {
         @Override
         public int getSize() {
            return var3;
         }

         @Override
         public ECPoint lookup(int var1) {
            int[] var2x = Nat.create(17);
            int[] var3x = Nat.create(17);
            byte var4x = 0;

            for (int var5x = 0; var5x < var3; var5x++) {
               int var6 = (var5x ^ var1) - 1 >> 31;

               for (int var7 = 0; var7 < 17; var7++) {
                  var2x[var7] ^= var4[var4x + var7] & var6;
                  var3x[var7] ^= var4[var4x + 17 + var7] & var6;
               }

               var4x += 34;
            }

            return this.createPoint(var2x, var3x);
         }

         @Override
         public ECPoint lookupVar(int var1) {
            int[] var2x = Nat.create(17);
            int[] var3x = Nat.create(17);
            int var4x = var1 * 17 * 2;

            for (int var5x = 0; var5x < 17; var5x++) {
               var2x[var5x] ^= var4[var4x + var5x];
               var3x[var5x] ^= var4[var4x + 17 + var5x];
            }

            return this.createPoint(var2x, var3x);
         }

         private ECPoint createPoint(int[] var1, int[] var2x) {
            return SecP521R1Curve.this.createRawPoint(new SecP521R1FieldElement(var1), new SecP521R1FieldElement(var2x), SecP521R1Curve.SECP521R1_AFFINE_ZS);
         }
      };
   }

   @Override
   public ECFieldElement randomFieldElement(SecureRandom var1) {
      int[] var2 = Nat.create(17);
      SecP521R1Field.random(var1, var2);
      return new SecP521R1FieldElement(var2);
   }

   @Override
   public ECFieldElement randomFieldElementMult(SecureRandom var1) {
      int[] var2 = Nat.create(17);
      SecP521R1Field.randomMult(var1, var2);
      return new SecP521R1FieldElement(var2);
   }
}
