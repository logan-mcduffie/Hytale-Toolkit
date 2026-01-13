package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DHParameters;

public class DHParametersGenerator {
   private int size;
   private int certainty;
   private SecureRandom random;
   private static final BigInteger TWO = BigInteger.valueOf(2L);

   public void init(int var1, int var2, SecureRandom var3) {
      this.size = var1;
      this.certainty = var2;
      this.random = var3;
   }

   public DHParameters generateParameters() {
      BigInteger[] var1 = DHParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
      BigInteger var2 = var1[0];
      BigInteger var3 = var1[1];
      BigInteger var4 = DHParametersHelper.selectGenerator(var2, var3, this.random);
      return new DHParameters(var2, var4, var3, TWO, null);
   }
}
