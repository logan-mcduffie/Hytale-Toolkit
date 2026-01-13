package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindedEngine implements AsymmetricBlockCipher {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private RSACoreEngine core = new RSACoreEngine();
   private RSAKeyParameters key;
   private SecureRandom random;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      SecureRandom var3 = null;
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var4 = (ParametersWithRandom)var2;
         var3 = var4.getRandom();
         var2 = var4.getParameters();
      }

      this.core.init(var1, var2);
      this.key = (RSAKeyParameters)var2;
      this.random = this.initSecureRandom(this.key instanceof RSAPrivateCrtKeyParameters, var3);
   }

   @Override
   public int getInputBlockSize() {
      return this.core.getInputBlockSize();
   }

   @Override
   public int getOutputBlockSize() {
      return this.core.getOutputBlockSize();
   }

   @Override
   public byte[] processBlock(byte[] var1, int var2, int var3) {
      if (this.key == null) {
         throw new IllegalStateException("RSA engine not initialised");
      } else {
         BigInteger var4 = this.core.convertInput(var1, var2, var3);
         BigInteger var5 = this.processInput(var4);
         return this.core.convertOutput(var5);
      }
   }

   protected SecureRandom initSecureRandom(boolean var1, SecureRandom var2) {
      return var1 ? CryptoServicesRegistrar.getSecureRandom(var2) : null;
   }

   private BigInteger processInput(BigInteger var1) {
      if (this.key instanceof RSAPrivateCrtKeyParameters) {
         RSAPrivateCrtKeyParameters var2 = (RSAPrivateCrtKeyParameters)this.key;
         BigInteger var3 = var2.getPublicExponent();
         if (var3 != null) {
            BigInteger var4 = var2.getModulus();
            BigInteger var5 = BigIntegers.createRandomInRange(ONE, var4.subtract(ONE), this.random);
            BigInteger var6 = var5.modPow(var3, var4);
            BigInteger var7 = BigIntegers.modOddInverse(var4, var5);
            BigInteger var8 = var6.multiply(var1).mod(var4);
            BigInteger var9 = this.core.processBlock(var8);
            return var7.multiply(var9).mod(var4);
         }
      }

      return this.core.processBlock(var1);
   }
}
