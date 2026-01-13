package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class FrodoKeyGenerationParameters extends KeyGenerationParameters {
   private FrodoParameters params;

   public FrodoKeyGenerationParameters(SecureRandom var1, FrodoParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public FrodoParameters getParameters() {
      return this.params;
   }
}
