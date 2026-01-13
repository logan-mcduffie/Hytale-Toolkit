package org.bouncycastle.crypto.params;

public class DHKeyParameters extends AsymmetricKeyParameter {
   private DHParameters params;

   protected DHKeyParameters(boolean var1, DHParameters var2) {
      super(var1);
      this.params = var2;
   }

   public DHParameters getParameters() {
      return this.params;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DHKeyParameters)) {
         return false;
      } else {
         DHKeyParameters var2 = (DHKeyParameters)var1;
         return this.params == null ? var2.getParameters() == null : this.params.equals(var2.getParameters());
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.isPrivate() ? 0 : 1;
      if (this.params != null) {
         var1 ^= this.params.hashCode();
      }

      return var1;
   }
}
