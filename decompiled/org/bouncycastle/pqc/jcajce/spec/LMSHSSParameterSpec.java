package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

/** @deprecated */
public class LMSHSSParameterSpec implements AlgorithmParameterSpec {
   private final LMSParameterSpec[] specs;

   public LMSHSSParameterSpec(LMSParameterSpec[] var1) {
      this.specs = (LMSParameterSpec[])var1.clone();
   }

   public LMSParameterSpec[] getLMSSpecs() {
      return (LMSParameterSpec[])this.specs.clone();
   }
}
