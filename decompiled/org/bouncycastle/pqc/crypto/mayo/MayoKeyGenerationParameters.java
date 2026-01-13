package org.bouncycastle.pqc.crypto.mayo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class MayoKeyGenerationParameters extends KeyGenerationParameters {
   private final MayoParameters params;

   public MayoKeyGenerationParameters(SecureRandom var1, MayoParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public MayoParameters getParameters() {
      return this.params;
   }
}
