package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;

public class MGFParameters implements DerivationParameters {
   byte[] seed;

   public MGFParameters(byte[] var1) {
      this(var1, 0, var1.length);
   }

   public MGFParameters(byte[] var1, int var2, int var3) {
      this.seed = new byte[var3];
      System.arraycopy(var1, var2, this.seed, 0, var3);
   }

   public byte[] getSeed() {
      return this.seed;
   }
}
