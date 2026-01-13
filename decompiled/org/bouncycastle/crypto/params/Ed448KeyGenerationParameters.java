package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class Ed448KeyGenerationParameters extends KeyGenerationParameters {
   public Ed448KeyGenerationParameters(SecureRandom var1) {
      super(var1, 448);
   }
}
