package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class DSAKeyGenerationParameters extends KeyGenerationParameters {
   private DSAParameters params;

   public DSAKeyGenerationParameters(SecureRandom var1, DSAParameters var2) {
      super(var1, var2.getP().bitLength() - 1);
      this.params = var2;
   }

   public DSAParameters getParameters() {
      return this.params;
   }
}
