package org.bouncycastle.pqc.crypto.xwing;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class XWingKeyGenerationParameters extends KeyGenerationParameters {
   public XWingKeyGenerationParameters(SecureRandom var1) {
      super(var1, 128);
   }
}
