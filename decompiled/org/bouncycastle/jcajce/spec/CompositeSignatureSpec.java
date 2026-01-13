package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class CompositeSignatureSpec implements AlgorithmParameterSpec {
   private final boolean isPrehashMode;
   private final AlgorithmParameterSpec secondaryParameterSpec;

   public CompositeSignatureSpec(boolean var1) {
      this(var1, null);
   }

   public CompositeSignatureSpec(boolean var1, AlgorithmParameterSpec var2) {
      this.isPrehashMode = var1;
      this.secondaryParameterSpec = var2;
   }

   public boolean isPrehashMode() {
      return this.isPrehashMode;
   }

   public AlgorithmParameterSpec getSecondarySpec() {
      return this.secondaryParameterSpec;
   }
}
