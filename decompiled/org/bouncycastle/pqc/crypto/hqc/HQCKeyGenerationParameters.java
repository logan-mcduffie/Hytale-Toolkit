package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class HQCKeyGenerationParameters extends KeyGenerationParameters {
   private final HQCParameters params;

   public HQCKeyGenerationParameters(SecureRandom var1, HQCParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public HQCParameters getParameters() {
      return this.params;
   }
}
