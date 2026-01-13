package org.bouncycastle.pqc.crypto.picnic;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class PicnicKeyGenerationParameters extends KeyGenerationParameters {
   private final PicnicParameters parameters;

   public PicnicKeyGenerationParameters(SecureRandom var1, PicnicParameters var2) {
      super(var1, -1);
      this.parameters = var2;
   }

   public PicnicParameters getParameters() {
      return this.parameters;
   }
}
