package org.bouncycastle.crypto.params;

public class ECKeyParameters extends AsymmetricKeyParameter {
   private final ECDomainParameters parameters;

   protected ECKeyParameters(boolean var1, ECDomainParameters var2) {
      super(var1);
      if (null == var2) {
         throw new NullPointerException("'parameters' cannot be null");
      } else {
         this.parameters = var2;
      }
   }

   public ECDomainParameters getParameters() {
      return this.parameters;
   }
}
