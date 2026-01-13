package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.math.Primes;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

public class RSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private RSAKeyGenerationParameters param;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (RSAKeyGenerationParameters)var1;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties("RSAKeyGen", ConstraintUtils.bitsOfSecurityForFF(var1.getStrength()), null, CryptoServicePurpose.KEYGEN)
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      AsymmetricCipherKeyPair var1 = null;
      boolean var2 = false;
      int var3 = this.param.getStrength();
      int var4 = (var3 + 1) / 2;
      int var5 = var3 - var4;
      int var6 = var3 / 2 - 100;
      if (var6 < var3 / 3) {
         var6 = var3 / 3;
      }

      int var7 = var3 >> 2;
      BigInteger var8 = BigInteger.valueOf(2L).pow(var3 / 2);
      BigInteger var9 = ONE.shiftLeft(var3 - 1);
      BigInteger var10 = ONE.shiftLeft(var6);

      while (!var2) {
         BigInteger var15 = this.param.getPublicExponent();
         BigInteger var11 = this.chooseRandomPrime(var4, var15, var9);

         while (true) {
            BigInteger var12 = this.chooseRandomPrime(var5, var15, var9);
            BigInteger var20 = var12.subtract(var11).abs();
            if (var20.bitLength() >= var6 && var20.compareTo(var10) > 0) {
               BigInteger var13 = var11.multiply(var12);
               if (var13.bitLength() != var3) {
                  var11 = var11.max(var12);
               } else {
                  if (WNafUtil.getNafWeight(var13) >= var7) {
                     if (var11.compareTo(var12) < 0) {
                        BigInteger var18 = var11;
                        var11 = var12;
                        var12 = var18;
                     }

                     BigInteger var16 = var11.subtract(ONE);
                     BigInteger var17 = var12.subtract(ONE);
                     BigInteger var23 = var16.gcd(var17);
                     BigInteger var19 = var16.divide(var23).multiply(var17);
                     BigInteger var14 = var15.modInverse(var19);
                     if (var14.compareTo(var8) > 0) {
                        var2 = true;
                        var20 = var14.remainder(var16);
                        BigInteger var21 = var14.remainder(var17);
                        BigInteger var22 = BigIntegers.modOddInverse(var11, var12);
                        var1 = new AsymmetricCipherKeyPair(
                           (AsymmetricKeyParameter)(new RSAKeyParameters(false, var13, var15, true)),
                           (AsymmetricKeyParameter)(new RSAPrivateCrtKeyParameters(var13, var15, var14, var11, var12, var20, var21, var22, true))
                        );
                     }
                     break;
                  }

                  var11 = this.chooseRandomPrime(var4, var15, var9);
               }
            }
         }
      }

      return var1;
   }

   protected BigInteger chooseRandomPrime(int var1, BigInteger var2, BigInteger var3) {
      for (int var4 = 0; var4 != 5 * var1; var4++) {
         BigInteger var5 = BigIntegers.createRandomPrime(var1, 1, this.param.getRandom());
         if (!var5.mod(var2).equals(ONE) && var5.multiply(var5).compareTo(var3) >= 0 && this.isProbablePrime(var5) && var2.gcd(var5.subtract(ONE)).equals(ONE)) {
            return var5;
         }
      }

      throw new IllegalStateException("unable to generate prime number for RSA key");
   }

   protected boolean isProbablePrime(BigInteger var1) {
      int var2 = getNumberOfIterations(var1.bitLength(), this.param.getCertainty());
      return !Primes.hasAnySmallFactors(var1) && Primes.isMRProbablePrime(var1, this.param.getRandom(), var2);
   }

   private static int getNumberOfIterations(int var0, int var1) {
      if (var0 >= 1536) {
         return var1 <= 100 ? 3 : (var1 <= 128 ? 4 : 4 + (var1 - 128 + 1) / 2);
      } else if (var0 >= 1024) {
         return var1 <= 100 ? 4 : (var1 <= 112 ? 5 : 5 + (var1 - 112 + 1) / 2);
      } else if (var0 >= 512) {
         return var1 <= 80 ? 5 : (var1 <= 100 ? 7 : 7 + (var1 - 100 + 1) / 2);
      } else {
         return var1 <= 80 ? 40 : 40 + (var1 - 80 + 1) / 2;
      }
   }
}
