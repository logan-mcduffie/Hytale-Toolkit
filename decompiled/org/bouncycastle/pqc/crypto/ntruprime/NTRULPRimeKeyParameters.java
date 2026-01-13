package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class NTRULPRimeKeyParameters extends AsymmetricKeyParameter {
   private final NTRULPRimeParameters params;

   public NTRULPRimeKeyParameters(boolean var1, NTRULPRimeParameters var2) {
      super(var1);
      this.params = var2;
   }

   public NTRULPRimeParameters getParameters() {
      return this.params;
   }
}
