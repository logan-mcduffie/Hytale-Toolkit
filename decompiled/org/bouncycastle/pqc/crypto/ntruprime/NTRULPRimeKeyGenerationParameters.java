package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class NTRULPRimeKeyGenerationParameters extends KeyGenerationParameters {
   private final NTRULPRimeParameters ntrulprParams;

   public NTRULPRimeKeyGenerationParameters(SecureRandom var1, NTRULPRimeParameters var2) {
      super(null != var1 ? var1 : CryptoServicesRegistrar.getSecureRandom(), 256);
      this.ntrulprParams = var2;
   }

   public NTRULPRimeParameters getNtrulprParams() {
      return this.ntrulprParams;
   }
}
