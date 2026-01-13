package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

class DHKeyGeneratorHelper {
   static final DHKeyGeneratorHelper INSTANCE = new DHKeyGeneratorHelper();
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final BigInteger TWO = BigInteger.valueOf(2L);

   private DHKeyGeneratorHelper() {
   }

   BigInteger calculatePrivate(DHParameters var1, SecureRandom var2) {
      int var3 = var1.getL();
      if (var3 != 0) {
         int var10 = var3 >>> 2;

         BigInteger var11;
         do {
            var11 = BigIntegers.createRandomBigInteger(var3, var2).setBit(var3 - 1);
         } while (WNafUtil.getNafWeight(var11) < var10);

         return var11;
      } else {
         BigInteger var4 = TWO;
         int var5 = var1.getM();
         if (var5 != 0) {
            var4 = ONE.shiftLeft(var5 - 1);
         }

         BigInteger var6 = var1.getQ();
         if (var6 == null) {
            var6 = var1.getP();
         }

         BigInteger var7 = var6.subtract(TWO);
         int var8 = var7.bitLength() >>> 2;

         BigInteger var9;
         do {
            var9 = BigIntegers.createRandomInRange(var4, var7, var2);
         } while (WNafUtil.getNafWeight(var9) < var8);

         return var9;
      }
   }

   BigInteger calculatePublic(DHParameters var1, BigInteger var2) {
      return var1.getG().modPow(var2, var1.getP());
   }
}
