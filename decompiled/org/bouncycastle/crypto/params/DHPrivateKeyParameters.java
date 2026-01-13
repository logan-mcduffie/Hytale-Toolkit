package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class DHPrivateKeyParameters extends DHKeyParameters {
   private BigInteger x;

   public DHPrivateKeyParameters(BigInteger var1, DHParameters var2) {
      super(true, var2);
      this.x = var1;
   }

   public BigInteger getX() {
      return this.x;
   }

   @Override
   public int hashCode() {
      return this.x.hashCode() ^ super.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DHPrivateKeyParameters)) {
         return false;
      } else {
         DHPrivateKeyParameters var2 = (DHPrivateKeyParameters)var1;
         return var2.getX().equals(this.x) && super.equals(var1);
      }
   }
}
