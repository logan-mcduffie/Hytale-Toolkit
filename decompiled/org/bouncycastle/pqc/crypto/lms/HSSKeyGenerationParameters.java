package org.bouncycastle.pqc.crypto.lms;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class HSSKeyGenerationParameters extends KeyGenerationParameters {
   private final LMSParameters[] lmsParameters;

   public HSSKeyGenerationParameters(LMSParameters[] var1, SecureRandom var2) {
      super(var2, LmsUtils.calculateStrength(var1[0]));
      if (var1.length != 0 && var1.length <= 8) {
         this.lmsParameters = var1;
      } else {
         throw new IllegalArgumentException("lmsParameters length should be between 1 and 8 inclusive");
      }
   }

   public int getDepth() {
      return this.lmsParameters.length;
   }

   public LMSParameters[] getLmsParameters() {
      return this.lmsParameters;
   }
}
