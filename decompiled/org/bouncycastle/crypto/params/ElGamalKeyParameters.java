package org.bouncycastle.crypto.params;

public class ElGamalKeyParameters extends AsymmetricKeyParameter {
   private ElGamalParameters params;

   protected ElGamalKeyParameters(boolean var1, ElGamalParameters var2) {
      super(var1);
      this.params = var2;
   }

   public ElGamalParameters getParameters() {
      return this.params;
   }

   @Override
   public int hashCode() {
      return this.params != null ? this.params.hashCode() : 0;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof ElGamalKeyParameters)) {
         return false;
      } else {
         ElGamalKeyParameters var2 = (ElGamalKeyParameters)var1;
         return this.params == null ? var2.getParameters() == null : this.params.equals(var2.getParameters());
      }
   }
}
