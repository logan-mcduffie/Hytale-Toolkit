package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class GOST3410KeyGenerationParameters extends KeyGenerationParameters {
   private GOST3410Parameters params;

   public GOST3410KeyGenerationParameters(SecureRandom var1, GOST3410Parameters var2) {
      super(var1, var2.getP().bitLength() - 1);
      this.params = var2;
   }

   public GOST3410Parameters getParameters() {
      return this.params;
   }
}
