package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class GOST3410PublicKeyParameters extends GOST3410KeyParameters {
   private BigInteger y;

   public GOST3410PublicKeyParameters(BigInteger var1, GOST3410Parameters var2) {
      super(false, var2);
      this.y = var1;
   }

   public BigInteger getY() {
      return this.y;
   }
}
