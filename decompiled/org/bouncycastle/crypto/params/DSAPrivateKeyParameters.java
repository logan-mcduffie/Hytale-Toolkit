package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class DSAPrivateKeyParameters extends DSAKeyParameters {
   private BigInteger x;

   public DSAPrivateKeyParameters(BigInteger var1, DSAParameters var2) {
      super(true, var2);
      this.x = var1;
   }

   public BigInteger getX() {
      return this.x;
   }
}
