package org.bouncycastle.jce.spec;

import java.security.spec.KeySpec;

public class ECKeySpec implements KeySpec {
   private ECParameterSpec spec;

   protected ECKeySpec(ECParameterSpec var1) {
      this.spec = var1;
   }

   public ECParameterSpec getParams() {
      return this.spec;
   }
}
