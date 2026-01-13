package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class CramerShoupKeyGenerationParameters extends KeyGenerationParameters {
   private CramerShoupParameters params;

   public CramerShoupKeyGenerationParameters(SecureRandom var1, CramerShoupParameters var2) {
      super(var1, getStrength(var2));
      this.params = var2;
   }

   public CramerShoupParameters getParameters() {
      return this.params;
   }

   static int getStrength(CramerShoupParameters var0) {
      return var0.getP().bitLength();
   }
}
