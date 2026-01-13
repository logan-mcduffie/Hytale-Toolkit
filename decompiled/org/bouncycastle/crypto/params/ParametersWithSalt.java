package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithSalt implements CipherParameters {
   private byte[] salt;
   private CipherParameters parameters;

   public ParametersWithSalt(CipherParameters var1, byte[] var2) {
      this(var1, var2, 0, var2.length);
   }

   public ParametersWithSalt(CipherParameters var1, byte[] var2, int var3, int var4) {
      this.salt = new byte[var4];
      this.parameters = var1;
      System.arraycopy(var2, var3, this.salt, 0, var4);
   }

   public byte[] getSalt() {
      return this.salt;
   }

   public CipherParameters getParameters() {
      return this.parameters;
   }
}
