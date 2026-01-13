package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.util.BigIntegers;

public class CramerShoupParametersGenerator {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private int size;
   private int certainty;
   private SecureRandom random;

   public void init(int var1, int var2, SecureRandom var3) {
      this.size = var1;
      this.certainty = var2;
      this.random = var3;
   }

   public CramerShoupParameters generateParameters() {
      BigInteger[] var1 = CramerShoupParametersGenerator.ParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
      BigInteger var2 = var1[1];
      BigInteger var3 = CramerShoupParametersGenerator.ParametersHelper.selectGenerator(var2, this.random);
      BigInteger var4 = CramerShoupParametersGenerator.ParametersHelper.selectGenerator(var2, this.random);

      while (var3.equals(var4)) {
         var4 = CramerShoupParametersGenerator.ParametersHelper.selectGenerator(var2, this.random);
      }

      return new CramerShoupParameters(var2, var3, var4, SHA256Digest.newInstance());
   }

   public CramerShoupParameters generateParameters(DHParameters var1) {
      BigInteger var2 = var1.getP();
      BigInteger var3 = var1.getG();
      BigInteger var4 = CramerShoupParametersGenerator.ParametersHelper.selectGenerator(var2, this.random);

      while (var3.equals(var4)) {
         var4 = CramerShoupParametersGenerator.ParametersHelper.selectGenerator(var2, this.random);
      }

      return new CramerShoupParameters(var2, var3, var4, SHA256Digest.newInstance());
   }

   private static class ParametersHelper {
      private static final BigInteger TWO = BigInteger.valueOf(2L);

      static BigInteger[] generateSafePrimes(int var0, int var1, SecureRandom var2) {
         int var5 = var0 - 1;

         BigInteger var3;
         BigInteger var4;
         do {
            var4 = BigIntegers.createRandomPrime(var5, 2, var2);
            var3 = var4.shiftLeft(1).add(CramerShoupParametersGenerator.ONE);
         } while (!var3.isProbablePrime(var1) || var1 > 2 && !var4.isProbablePrime(var1));

         return new BigInteger[]{var3, var4};
      }

      static BigInteger selectGenerator(BigInteger var0, SecureRandom var1) {
         BigInteger var2 = var0.subtract(TWO);

         BigInteger var3;
         do {
            BigInteger var4 = BigIntegers.createRandomInRange(TWO, var2, var1);
            var3 = var4.modPow(TWO, var0);
         } while (var3.equals(CramerShoupParametersGenerator.ONE));

         return var3;
      }
   }
}
