package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SPHINCSPlusKeyParameters extends AsymmetricKeyParameter {
   final SPHINCSPlusParameters parameters;

   protected SPHINCSPlusKeyParameters(boolean var1, SPHINCSPlusParameters var2) {
      super(var1);
      this.parameters = var2;
   }

   public SPHINCSPlusParameters getParameters() {
      return this.parameters;
   }
}
