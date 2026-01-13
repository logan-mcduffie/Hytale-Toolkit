package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public abstract class NTRUKeyParameters extends AsymmetricKeyParameter {
   private final NTRUParameters params;

   NTRUKeyParameters(boolean var1, NTRUParameters var2) {
      super(var1);
      this.params = var2;
   }

   public NTRUParameters getParameters() {
      return this.params;
   }
}
