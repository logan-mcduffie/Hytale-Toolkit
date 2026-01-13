package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public final class XMSSMTKeyGenerationParameters extends KeyGenerationParameters {
   private final XMSSMTParameters xmssmtParameters;

   public XMSSMTKeyGenerationParameters(XMSSMTParameters var1, SecureRandom var2) {
      super(var2, -1);
      this.xmssmtParameters = var1;
   }

   public XMSSMTParameters getParameters() {
      return this.xmssmtParameters;
   }
}
