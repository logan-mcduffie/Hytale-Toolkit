package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindingFactorGenerator {
   private static BigInteger TWO = BigInteger.valueOf(2L);
   private RSAKeyParameters key;
   private SecureRandom random;

   public void init(CipherParameters var1) {
      if (var1 instanceof ParametersWithRandom) {
         ParametersWithRandom var2 = (ParametersWithRandom)var1;
         this.key = (RSAKeyParameters)var2.getParameters();
         this.random = var2.getRandom();
      } else {
         this.key = (RSAKeyParameters)var1;
         this.random = CryptoServicesRegistrar.getSecureRandom();
      }

      if (this.key instanceof RSAPrivateCrtKeyParameters) {
         throw new IllegalArgumentException("generator requires RSA public key");
      }
   }

   public BigInteger generateBlindingFactor() {
      if (this.key == null) {
         throw new IllegalStateException("generator not initialised");
      } else {
         BigInteger var1 = this.key.getModulus();
         int var2 = var1.bitLength() - 1;

         BigInteger var3;
         do {
            var3 = BigIntegers.createRandomBigInteger(var2, this.random);
         } while (var3.compareTo(TWO) < 0 || !BigIntegers.modOddIsCoprime(var1, var3));

         return var3;
      }
   }
}
