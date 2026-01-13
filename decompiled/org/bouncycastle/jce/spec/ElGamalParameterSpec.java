package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;

public class ElGamalParameterSpec implements AlgorithmParameterSpec {
   private BigInteger p;
   private BigInteger g;

   public ElGamalParameterSpec(BigInteger var1, BigInteger var2) {
      this.p = var1;
      this.g = var2;
   }

   public BigInteger getP() {
      return this.p;
   }

   public BigInteger getG() {
      return this.g;
   }
}
