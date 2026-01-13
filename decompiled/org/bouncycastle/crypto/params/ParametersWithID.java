package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithID implements CipherParameters {
   private CipherParameters parameters;
   private byte[] id;

   public ParametersWithID(CipherParameters var1, byte[] var2) {
      this.parameters = var1;
      this.id = var2;
   }

   public byte[] getID() {
      return this.id;
   }

   public CipherParameters getParameters() {
      return this.parameters;
   }
}
