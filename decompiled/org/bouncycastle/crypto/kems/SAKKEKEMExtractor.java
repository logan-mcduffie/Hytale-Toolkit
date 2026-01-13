package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.params.SAKKEPrivateKeyParameters;
import org.bouncycastle.crypto.params.SAKKEPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class SAKKEKEMExtractor implements EncapsulatedSecretExtractor {
   private final ECCurve curve;
   private final BigInteger p;
   private final BigInteger q;
   private final ECPoint P;
   private final ECPoint Z_S;
   private final ECPoint K_bs;
   private final int n;
   private final BigInteger identifier;
   private final Digest digest;

   public SAKKEKEMExtractor(SAKKEPrivateKeyParameters var1) {
      SAKKEPublicKeyParameters var2 = var1.getPublicParams();
      this.curve = var2.getCurve();
      this.q = var2.getQ();
      this.P = var2.getPoint();
      this.p = var2.getPrime();
      this.Z_S = var2.getZ();
      this.identifier = var2.getIdentifier();
      this.K_bs = this.P.multiply(this.identifier.add(var1.getMasterSecret()).modInverse(this.q)).normalize();
      this.n = var2.getN();
      this.digest = var2.getDigest();
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      ECPoint var2 = this.curve.decodePoint(Arrays.copyOfRange(var1, 0, 257));
      BigInteger var3 = BigIntegers.fromUnsignedByteArray(var1, 257, 16);
      BigInteger var4 = computePairing(var2, this.K_bs, this.p, this.q);
      BigInteger var5 = BigInteger.ONE.shiftLeft(this.n);
      BigInteger var6 = SAKKEKEMSGenerator.hashToIntegerRange(var4.toByteArray(), var5, this.digest);
      BigInteger var7 = var3.xor(var6).mod(this.p);
      BigInteger var8 = this.identifier;
      BigInteger var9 = SAKKEKEMSGenerator.hashToIntegerRange(Arrays.concatenate(var7.toByteArray(), var8.toByteArray()), this.q, this.digest);
      BigInteger var11 = this.curve.getOrder();
      ECPoint var10;
      if (var11 == null) {
         var10 = this.P.multiply(var8).add(this.Z_S).multiply(var9);
      } else {
         BigInteger var12 = var8.multiply(var9).mod(var11);
         var10 = ECAlgorithms.sumOfTwoMultiplies(this.P, var12, this.Z_S, var9);
      }

      var10 = var10.subtract(var2);
      if (!var10.isInfinity()) {
         throw new IllegalStateException("Validation of R_bS failed");
      } else {
         return BigIntegers.asUnsignedByteArray(this.n / 8, var7);
      }
   }

   @Override
   public int getEncapsulationLength() {
      return 273;
   }

   static BigInteger computePairing(ECPoint var0, ECPoint var1, BigInteger var2, BigInteger var3) {
      BigInteger[] var4 = new BigInteger[]{BigInteger.ONE, BigInteger.ZERO};
      ECPoint var5 = var0;
      BigInteger var6 = var3.subtract(BigInteger.ONE);
      int var7 = var6.bitLength();
      BigInteger var8 = var1.getAffineXCoord().toBigInteger();
      BigInteger var9 = var1.getAffineYCoord().toBigInteger();
      BigInteger var10 = var0.getAffineXCoord().toBigInteger();
      BigInteger var11 = var0.getAffineYCoord().toBigInteger();
      BigInteger var12 = BigInteger.valueOf(3L);

      for (int var13 = var7 - 2; var13 >= 0; var13--) {
         BigInteger var14 = var5.getAffineXCoord().toBigInteger();
         BigInteger var15 = var5.getAffineYCoord().toBigInteger();
         BigInteger var16 = var14.multiply(var14)
            .mod(var2)
            .subtract(BigInteger.ONE)
            .multiply(var12)
            .multiply(BigIntegers.modOddInverse(var2, var15.shiftLeft(1)))
            .mod(var2);
         var4 = fp2PointSquare(var4[0], var4[1], var2);
         var4 = fp2Multiply(var4[0], var4[1], var16.multiply(var8.add(var14)).subtract(var15).mod(var2), var9, var2);
         var5 = var5.twice().normalize();
         if (var6.testBit(var13)) {
            var14 = var5.getAffineXCoord().toBigInteger();
            var15 = var5.getAffineYCoord().toBigInteger();
            var16 = var15.subtract(var11).multiply(BigIntegers.modOddInverse(var2, var14.subtract(var10))).mod(var2);
            var4 = fp2Multiply(var4[0], var4[1], var16.multiply(var8.add(var14)).subtract(var15).mod(var2), var9, var2);
            if (var13 > 0) {
               var5 = var5.add(var0).normalize();
            }
         }
      }

      var4 = fp2PointSquare(var4[0], var4[1], var2);
      var4 = fp2PointSquare(var4[0], var4[1], var2);
      BigInteger var20 = BigIntegers.modOddInverse(var2, var4[0]);
      return var4[1].multiply(var20).mod(var2);
   }

   static BigInteger[] fp2Multiply(BigInteger var0, BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      return new BigInteger[]{var0.multiply(var2).subtract(var1.multiply(var3)).mod(var4), var0.multiply(var3).add(var1.multiply(var2)).mod(var4)};
   }

   static BigInteger[] fp2PointSquare(BigInteger var0, BigInteger var1, BigInteger var2) {
      return new BigInteger[]{var0.add(var1).multiply(var0.subtract(var1)).mod(var2), var0.multiply(var1).shiftLeft(1).mod(var2)};
   }
}
