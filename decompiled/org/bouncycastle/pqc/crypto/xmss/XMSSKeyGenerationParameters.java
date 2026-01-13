package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public final class XMSSKeyGenerationParameters extends KeyGenerationParameters {
   private final XMSSParameters xmssParameters;

   public XMSSKeyGenerationParameters(XMSSParameters var1, SecureRandom var2) {
      super(var2, -1);
      this.xmssParameters = var1;
   }

   public XMSSParameters getParameters() {
      return this.xmssParameters;
   }
}
