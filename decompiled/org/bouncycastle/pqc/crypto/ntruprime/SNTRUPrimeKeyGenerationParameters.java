package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SNTRUPrimeKeyGenerationParameters extends KeyGenerationParameters {
   private final SNTRUPrimeParameters sntrupParams;

   public SNTRUPrimeKeyGenerationParameters(SecureRandom var1, SNTRUPrimeParameters var2) {
      super(null != var1 ? var1 : CryptoServicesRegistrar.getSecureRandom(), 256);
      this.sntrupParams = var2;
   }

   public SNTRUPrimeParameters getSntrupParams() {
      return this.sntrupParams;
   }
}
