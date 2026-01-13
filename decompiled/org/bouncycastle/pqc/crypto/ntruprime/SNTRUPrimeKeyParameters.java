package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SNTRUPrimeKeyParameters extends AsymmetricKeyParameter {
   private final SNTRUPrimeParameters params;

   public SNTRUPrimeKeyParameters(boolean var1, SNTRUPrimeParameters var2) {
      super(var1);
      this.params = var2;
   }

   public SNTRUPrimeParameters getParameters() {
      return this.params;
   }
}
