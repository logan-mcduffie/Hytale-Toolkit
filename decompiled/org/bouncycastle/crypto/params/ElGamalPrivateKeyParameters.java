package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class ElGamalPrivateKeyParameters extends ElGamalKeyParameters {
   private BigInteger x;

   public ElGamalPrivateKeyParameters(BigInteger var1, ElGamalParameters var2) {
      super(true, var2);
      this.x = var1;
   }

   public BigInteger getX() {
      return this.x;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof ElGamalPrivateKeyParameters)) {
         return false;
      } else {
         ElGamalPrivateKeyParameters var2 = (ElGamalPrivateKeyParameters)var1;
         return !var2.getX().equals(this.x) ? false : super.equals(var1);
      }
   }

   @Override
   public int hashCode() {
      return this.getX().hashCode();
   }
}
