package org.bouncycastle.pqc.crypto.lms;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class LMSKeyGenerationParameters extends KeyGenerationParameters {
   private final LMSParameters lmsParameters;

   public LMSKeyGenerationParameters(LMSParameters var1, SecureRandom var2) {
      super(var2, LmsUtils.calculateStrength(var1));
      this.lmsParameters = var1;
   }

   public LMSParameters getParameters() {
      return this.lmsParameters;
   }
}
