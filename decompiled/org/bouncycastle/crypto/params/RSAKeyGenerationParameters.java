package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class RSAKeyGenerationParameters extends KeyGenerationParameters {
   private BigInteger publicExponent;
   private int certainty;

   public RSAKeyGenerationParameters(BigInteger var1, SecureRandom var2, int var3, int var4) {
      super(var2, var3);
      if (var3 < 12) {
         throw new IllegalArgumentException("key strength too small");
      } else if (!var1.testBit(0)) {
         throw new IllegalArgumentException("public exponent cannot be even");
      } else {
         this.publicExponent = var1;
         this.certainty = var4;
      }
   }

   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }

   public int getCertainty() {
      return this.certainty;
   }
}
