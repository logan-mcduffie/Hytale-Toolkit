package org.bouncycastle.pqc.crypto.sphincsplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SPHINCSPlusKeyGenerationParameters extends KeyGenerationParameters {
   private final SPHINCSPlusParameters parameters;

   public SPHINCSPlusKeyGenerationParameters(SecureRandom var1, SPHINCSPlusParameters var2) {
      super(var1, -1);
      this.parameters = var2;
   }

   SPHINCSPlusParameters getParameters() {
      return this.parameters;
   }
}
