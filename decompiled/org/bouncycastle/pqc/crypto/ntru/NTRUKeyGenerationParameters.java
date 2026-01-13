package org.bouncycastle.pqc.crypto.ntru;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class NTRUKeyGenerationParameters extends KeyGenerationParameters {
   private final NTRUParameters ntruParameters;

   public NTRUKeyGenerationParameters(SecureRandom var1, NTRUParameters var2) {
      super(var1, 0);
      this.ntruParameters = var2;
   }

   public NTRUParameters getParameters() {
      return this.ntruParameters;
   }
}
