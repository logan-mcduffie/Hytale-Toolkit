package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class RainbowKeyParameters extends AsymmetricKeyParameter {
   private final RainbowParameters params;
   private final int docLength;

   public RainbowKeyParameters(boolean var1, RainbowParameters var2) {
      super(var1);
      this.params = var2;
      this.docLength = var2.getM();
   }

   public RainbowParameters getParameters() {
      return this.params;
   }

   public int getDocLength() {
      return this.docLength;
   }
}
