package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class TLSRSAPremasterSecretParameterSpec implements AlgorithmParameterSpec {
   private final int protocolVersion;

   public TLSRSAPremasterSecretParameterSpec(int var1) {
      this.protocolVersion = var1;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }
}
