package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindingEngine implements AsymmetricBlockCipher {
   private RSACoreEngine core = new RSACoreEngine();
   private RSAKeyParameters key;
   private BigInteger blindingFactor;
   private boolean forEncryption;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      RSABlindingParameters var3;
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var4 = (ParametersWithRandom)var2;
         var3 = (RSABlindingParameters)var4.getParameters();
      } else {
         var3 = (RSABlindingParameters)var2;
      }

      this.core.init(var1, var3.getPublicKey());
      this.forEncryption = var1;
      this.key = var3.getPublicKey();
      this.blindingFactor = var3.getBlindingFactor();
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
      BigInteger var4 = this.core.convertInput(var1, var2, var3);
      if (this.forEncryption) {
         var4 = this.blindMessage(var4);
      } else {
         var4 = this.unblindMessage(var4);
      }

      return this.core.convertOutput(var4);
   }

   private BigInteger blindMessage(BigInteger var1) {
      BigInteger var2 = this.blindingFactor;
      var2 = var1.multiply(var2.modPow(this.key.getExponent(), this.key.getModulus()));
      return var2.mod(this.key.getModulus());
   }

   private BigInteger unblindMessage(BigInteger var1) {
      BigInteger var2 = this.key.getModulus();
      BigInteger var4 = BigIntegers.modOddInverse(var2, this.blindingFactor);
      BigInteger var3 = var1.multiply(var4);
      return var3.mod(var2);
   }
}
