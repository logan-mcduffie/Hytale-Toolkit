package org.bouncycastle.crypto.params;

public class DSAKeyParameters extends AsymmetricKeyParameter {
   private DSAParameters params;

   public DSAKeyParameters(boolean var1, DSAParameters var2) {
      super(var1);
      this.params = var2;
   }

   public DSAParameters getParameters() {
      return this.params;
   }
}
