package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class LMSHSSKeyGenParameterSpec implements AlgorithmParameterSpec {
   private final LMSKeyGenParameterSpec[] specs;

   public LMSHSSKeyGenParameterSpec(LMSKeyGenParameterSpec... var1) {
      if (var1.length == 0) {
         throw new IllegalArgumentException("at least one LMSKeyGenParameterSpec required");
      } else {
         this.specs = (LMSKeyGenParameterSpec[])var1.clone();
      }
   }

   public LMSKeyGenParameterSpec[] getLMSSpecs() {
      return (LMSKeyGenParameterSpec[])this.specs.clone();
   }
}
