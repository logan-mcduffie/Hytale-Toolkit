package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class BIKEKeyGenerationParameters extends KeyGenerationParameters {
   private BIKEParameters params;

   public BIKEKeyGenerationParameters(SecureRandom var1, BIKEParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public BIKEParameters getParameters() {
      return this.params;
   }
}
