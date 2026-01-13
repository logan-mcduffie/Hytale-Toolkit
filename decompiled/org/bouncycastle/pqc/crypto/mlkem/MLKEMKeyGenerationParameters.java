package org.bouncycastle.pqc.crypto.mlkem;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class MLKEMKeyGenerationParameters extends KeyGenerationParameters {
   private final MLKEMParameters params;

   public MLKEMKeyGenerationParameters(SecureRandom var1, MLKEMParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public MLKEMParameters getParameters() {
      return this.params;
   }
}
