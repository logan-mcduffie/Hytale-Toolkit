package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SLHDSAKeyParameters extends AsymmetricKeyParameter {
   private final SLHDSAParameters parameters;

   protected SLHDSAKeyParameters(boolean var1, SLHDSAParameters var2) {
      super(var1);
      this.parameters = var2;
   }

   public SLHDSAParameters getParameters() {
      return this.parameters;
   }
}
