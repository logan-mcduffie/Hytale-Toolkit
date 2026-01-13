package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class DilithiumKeyGenerationParameters extends KeyGenerationParameters {
   private final DilithiumParameters params;

   public DilithiumKeyGenerationParameters(SecureRandom var1, DilithiumParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public DilithiumParameters getParameters() {
      return this.params;
   }
}
