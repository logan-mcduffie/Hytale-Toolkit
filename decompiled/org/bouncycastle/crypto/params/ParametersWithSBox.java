package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithSBox implements CipherParameters {
   private CipherParameters parameters;
   private byte[] sBox;

   public ParametersWithSBox(CipherParameters var1, byte[] var2) {
      this.parameters = var1;
      this.sBox = var2;
   }

   public byte[] getSBox() {
      return this.sBox;
   }

   public CipherParameters getParameters() {
      return this.parameters;
   }
}
