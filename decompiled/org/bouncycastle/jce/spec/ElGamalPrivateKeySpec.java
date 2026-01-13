package org.bouncycastle.jce.spec;

import java.math.BigInteger;

public class ElGamalPrivateKeySpec extends ElGamalKeySpec {
   private BigInteger x;

   public ElGamalPrivateKeySpec(BigInteger var1, ElGamalParameterSpec var2) {
      super(var2);
      this.x = var1;
   }

   public BigInteger getX() {
      return this.x;
   }
}
