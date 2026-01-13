package org.bouncycastle.pqc.crypto.cmce;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class CMCEKeyGenerationParameters extends KeyGenerationParameters {
   private CMCEParameters params;

   public CMCEKeyGenerationParameters(SecureRandom var1, CMCEParameters var2) {
      super(var1, 256);
      this.params = var2;
   }

   public CMCEParameters getParameters() {
      return this.params;
   }
}
