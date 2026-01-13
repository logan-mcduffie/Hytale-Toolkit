package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithUKM implements CipherParameters {
   private byte[] ukm;
   private CipherParameters parameters;

   public ParametersWithUKM(CipherParameters var1, byte[] var2) {
      this(var1, var2, 0, var2.length);
   }

   public ParametersWithUKM(CipherParameters var1, byte[] var2, int var3, int var4) {
      this.ukm = new byte[var4];
      this.parameters = var1;
      System.arraycopy(var2, var3, this.ukm, 0, var4);
   }

   public byte[] getUKM() {
      return this.ukm;
   }

   public CipherParameters getParameters() {
      return this.parameters;
   }
}
