package org.bouncycastle.pqc.crypto.snova;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SnovaKeyGenerationParameters extends KeyGenerationParameters {
   private final SnovaParameters params;

   public SnovaKeyGenerationParameters(SecureRandom var1, SnovaParameters var2) {
      super(var1, -1);
      this.params = var2;
   }

   public SnovaParameters getParameters() {
      return this.params;
   }
}
