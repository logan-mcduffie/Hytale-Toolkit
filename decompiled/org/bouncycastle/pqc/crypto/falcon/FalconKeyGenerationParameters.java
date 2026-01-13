package org.bouncycastle.pqc.crypto.falcon;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class FalconKeyGenerationParameters extends KeyGenerationParameters {
   private final FalconParameters params;

   public FalconKeyGenerationParameters(SecureRandom var1, FalconParameters var2) {
      super(var1, 320);
      this.params = var2;
   }

   public FalconParameters getParameters() {
      return this.params;
   }
}
