package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class RainbowKeyGenerationParameters extends KeyGenerationParameters {
   private RainbowParameters params;

   public RainbowKeyGenerationParameters(SecureRandom var1, RainbowParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public RainbowParameters getParameters() {
      return this.params;
   }
}
