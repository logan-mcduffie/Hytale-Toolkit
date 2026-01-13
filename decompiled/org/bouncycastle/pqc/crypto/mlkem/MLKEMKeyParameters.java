package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class MLKEMKeyParameters extends AsymmetricKeyParameter {
   private MLKEMParameters params;

   public MLKEMKeyParameters(boolean var1, MLKEMParameters var2) {
      super(var1);
      this.params = var2;
   }

   public MLKEMParameters getParameters() {
      return this.params;
   }
}
