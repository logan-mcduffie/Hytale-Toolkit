package org.bouncycastle.crypto.params;

public class GOST3410KeyParameters extends AsymmetricKeyParameter {
   private GOST3410Parameters params;

   public GOST3410KeyParameters(boolean var1, GOST3410Parameters var2) {
      super(var1);
      this.params = var2;
   }

   public GOST3410Parameters getParameters() {
      return this.params;
   }
}
