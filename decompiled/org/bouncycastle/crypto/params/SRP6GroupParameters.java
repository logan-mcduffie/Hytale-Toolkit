package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class SRP6GroupParameters {
   private BigInteger N;
   private BigInteger g;

   public SRP6GroupParameters(BigInteger var1, BigInteger var2) {
      this.N = var1;
      this.g = var2;
   }

   public BigInteger getG() {
      return this.g;
   }

   public BigInteger getN() {
      return this.N;
   }
}
