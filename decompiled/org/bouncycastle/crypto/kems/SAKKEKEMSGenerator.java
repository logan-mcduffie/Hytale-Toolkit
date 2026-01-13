package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.SAKKEPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class SAKKEKEMSGenerator implements EncapsulatedSecretGenerator {
   private final SecureRandom random;

   public SAKKEKEMSGenerator(SecureRandom var1) {
      this.random = var1;
   }

   @Override
   public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1) {
      SAKKEPublicKeyParameters var2 = (SAKKEPublicKeyParameters)var1;
      ECPoint var3 = var2.getZ();
      BigInteger var4 = var2.getIdentifier();
      BigInteger var5 = var2.getPrime();
      BigInteger var6 = var2.getQ();
      BigInteger var7 = var2.getG();
      int var8 = var2.getN();
      ECCurve var9 = var2.getCurve();
      ECPoint var10 = var2.getPoint();
      Digest var11 = var2.getDigest();
      BigInteger var12 = BigIntegers.createRandomBigInteger(var8, this.random);
      BigInteger var13 = hashToIntegerRange(Arrays.concatenate(var12.toByteArray(), var4.toByteArray()), var6, var11);
      BigInteger var15 = var9.getOrder();
      ECPoint var14;
      if (var15 == null) {
         var14 = var10.multiply(var4).add(var3).multiply(var13).normalize();
      } else {
         BigInteger var16 = var4.multiply(var13).mod(var15);
         var14 = ECAlgorithms.sumOfTwoMultiplies(var10, var16, var3, var13).normalize();
      }

      BigInteger var26 = BigInteger.ONE;
      BigInteger var17 = var7;
      BigInteger var18 = BigInteger.ONE;
      BigInteger var19 = var7;
      ECPoint var20 = var9.createPoint(var18, var7);

      for (int var21 = var13.bitLength() - 2; var21 >= 0; var21--) {
         BigInteger[] var22 = SAKKEKEMExtractor.fp2PointSquare(var18, var19, var5);
         var20 = var20.timesPow2(2);
         var18 = var22[0];
         var19 = var22[1];
         if (var13.testBit(var21)) {
            var22 = SAKKEKEMExtractor.fp2Multiply(var18, var19, var26, var17, var5);
            var18 = var22[0];
            var19 = var22[1];
         }
      }

      BigInteger var27 = BigIntegers.modOddInverse(var5, var18);
      BigInteger var29 = var19.multiply(var27).mod(var5);
      BigInteger var23 = hashToIntegerRange(var29.toByteArray(), BigInteger.ONE.shiftLeft(var8), var11);
      BigInteger var24 = var12.xor(var23);
      byte[] var25 = Arrays.concatenate(var14.getEncoded(false), BigIntegers.asUnsignedByteArray(16, var24));
      return new SecretWithEncapsulationImpl(BigIntegers.asUnsignedByteArray(var8 / 8, var12), var25);
   }

   static BigInteger hashToIntegerRange(byte[] var0, BigInteger var1, Digest var2) {
      byte[] var3 = new byte[var2.getDigestSize()];
      var2.update(var0, 0, var0.length);
      var2.doFinal(var3, 0);
      byte[] var4 = new byte[var2.getDigestSize()];
      int var5 = var1.bitLength() >> 8;
      BigInteger var6 = BigInteger.ZERO;
      byte[] var7 = new byte[var2.getDigestSize()];

      for (int var8 = 0; var8 <= var5; var8++) {
         var2.update(var4, 0, var4.length);
         var2.doFinal(var4, 0);
         var2.update(var4, 0, var4.length);
         var2.update(var3, 0, var3.length);
         var2.doFinal(var7, 0);
         var6 = var6.shiftLeft(var7.length * 8).add(new BigInteger(1, var7));
      }

      return var6.mod(var1);
   }
}
