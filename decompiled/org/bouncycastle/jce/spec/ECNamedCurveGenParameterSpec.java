package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class ECNamedCurveGenParameterSpec implements AlgorithmParameterSpec {
   private String name;

   public ECNamedCurveGenParameterSpec(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }
}
