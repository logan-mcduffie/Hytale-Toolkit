package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SABERKeyParameters extends AsymmetricKeyParameter {
   private SABERParameters params;

   public SABERKeyParameters(boolean var1, SABERParameters var2) {
      super(var1);
      this.params = var2;
   }

   public SABERParameters getParameters() {
      return this.params;
   }
}
