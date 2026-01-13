package org.bouncycastle.pqc.crypto.mldsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class MLDSAKeyGenerationParameters extends KeyGenerationParameters {
   private final MLDSAParameters params;

   public MLDSAKeyGenerationParameters(SecureRandom var1, MLDSAParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public MLDSAParameters getParameters() {
      return this.params;
   }
}
