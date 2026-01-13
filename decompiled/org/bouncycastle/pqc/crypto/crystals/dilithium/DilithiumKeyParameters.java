package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DilithiumKeyParameters extends AsymmetricKeyParameter {
   private final DilithiumParameters params;

   public DilithiumKeyParameters(boolean var1, DilithiumParameters var2) {
      super(var1);
      this.params = var2;
   }

   public DilithiumParameters getParameters() {
      return this.params;
   }
}
