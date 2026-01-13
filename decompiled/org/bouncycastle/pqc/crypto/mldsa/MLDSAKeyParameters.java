package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class MLDSAKeyParameters extends AsymmetricKeyParameter {
   private final MLDSAParameters params;

   public MLDSAKeyParameters(boolean var1, MLDSAParameters var2) {
      super(var1);
      this.params = var2;
   }

   public MLDSAParameters getParameters() {
      return this.params;
   }
}
