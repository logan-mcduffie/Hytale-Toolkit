package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class ElGamalGenParameterSpec implements AlgorithmParameterSpec {
   private int primeSize;

   public ElGamalGenParameterSpec(int var1) {
      this.primeSize = var1;
   }

   public int getPrimeSize() {
      return this.primeSize;
   }
}
