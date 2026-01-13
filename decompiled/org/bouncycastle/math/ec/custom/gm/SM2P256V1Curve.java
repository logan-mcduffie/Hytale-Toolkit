package org.bouncycastle.math.ec.custom.gm;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.encoders.Hex;

public class SM2P256V1Curve extends ECCurve.AbstractFp {
   public static final BigInteger q = SM2P256V1FieldElement.Q;
   private static final int SM2P256V1_DEFAULT_COORDS = 2;
   private static final ECFieldElement[] SM2P256V1_AFFINE_ZS = new ECFieldElement[]{new SM2P256V1FieldElement(ECConstants.ONE)};
   protected SM2P256V1Point infinity = new SM2P256V1Point(this, null, null);

   public SM2P256V1Curve() {
      super(q);
      this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC")));
      this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93")));
      this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123"));
      this.cofactor = BigInteger.valueOf(1L);
      this.coord = 2;
   }

   @Override
   protected ECCurve cloneCurve() {
      return new SM2P256V1Curve();
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
      return new SM2P256V1FieldElement(var1);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2) {
      return new SM2P256V1Point(this, var1, var2);
   }

   @Override
   protected ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3) {
      return new SM2P256V1Point(this, var1, var2, var3);
   }

   @Override
   public ECPoint getInfinity() {
      return this.infinity;
   }

   @Override
   public ECLookupTable createCacheSafeLookupTable(ECPoint[] var1, int var2, final int var3) {
      final int[] var4 = new int[var3 * 8 * 2];
      int var5 = 0;

      for (int var6 = 0; var6 < var3; var6++) {
         ECPoint var7 = var1[var2 + var6];
         Nat256.copy(((SM2P256V1FieldElement)var7.getRawXCoord()).x, 0, var4, var5);
         var5 += 8;
         Nat256.copy(((SM2P256V1FieldElement)var7.getRawYCoord()).x, 0, var4, var5);
         var5 += 8;
      }

      return new AbstractECLookupTable() {
         @Override
         public int getSize() {
            return var3;
         }

         @Override
         public ECPoint lookup(int var1) {
            int[] var2x = Nat256.create();
            int[] var3x = Nat256.create();
            byte var4x = 0;

            for (int var5x = 0; var5x < var3; var5x++) {
               int var6 = (var5x ^ var1) - 1 >> 31;

               for (int var7 = 0; var7 < 8; var7++) {
                  var2x[var7] ^= var4[var4x + var7] & var6;
                  var3x[var7] ^= var4[var4x + 8 + var7] & var6;
               }

               var4x += 16;
            }

            return this.createPoint(var2x, var3x);
         }

         @Override
         public ECPoint lookupVar(int var1) {
            int[] var2x = Nat256.create();
            int[] var3x = Nat256.create();
            int var4x = var1 * 8 * 2;

            for (int var5x = 0; var5x < 8; var5x++) {
               var2x[var5x] = var4[var4x + var5x];
               var3x[var5x] = var4[var4x + 8 + var5x];
            }

            return this.createPoint(var2x, var3x);
         }

         private ECPoint createPoint(int[] var1, int[] var2x) {
            return SM2P256V1Curve.this.createRawPoint(new SM2P256V1FieldElement(var1), new SM2P256V1FieldElement(var2x), SM2P256V1Curve.SM2P256V1_AFFINE_ZS);
         }
      };
   }

   @Override
   public ECFieldElement randomFieldElement(SecureRandom var1) {
      int[] var2 = Nat256.create();
      SM2P256V1Field.random(var1, var2);
      return new SM2P256V1FieldElement(var2);
   }

   @Override
   public ECFieldElement randomFieldElementMult(SecureRandom var1) {
      int[] var2 = Nat256.create();
      SM2P256V1Field.randomMult(var1, var2);
      return new SM2P256V1FieldElement(var2);
   }
}
