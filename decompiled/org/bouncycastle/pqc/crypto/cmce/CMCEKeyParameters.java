package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class CMCEKeyParameters extends AsymmetricKeyParameter {
   private CMCEParameters params;

   public CMCEKeyParameters(boolean var1, CMCEParameters var2) {
      super(var1);
      this.params = var2;
   }

   public CMCEParameters getParameters() {
      return this.params;
   }
}
