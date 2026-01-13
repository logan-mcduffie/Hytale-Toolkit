package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SABERKeyGenerationParameters extends KeyGenerationParameters {
   private SABERParameters params;

   public SABERKeyGenerationParameters(SecureRandom var1, SABERParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public SABERParameters getParameters() {
      return this.params;
   }
}
