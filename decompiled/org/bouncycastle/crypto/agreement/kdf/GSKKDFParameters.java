package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DerivationParameters;

public class GSKKDFParameters implements DerivationParameters {
   private final byte[] z;
   private final int startCounter;
   private final byte[] nonce;

   public GSKKDFParameters(byte[] var1, int var2) {
      this(var1, var2, null);
   }

   public GSKKDFParameters(byte[] var1, int var2, byte[] var3) {
      this.z = var1;
      this.startCounter = var2;
      this.nonce = var3;
   }

   public byte[] getZ() {
      return this.z;
   }

   public int getStartCounter() {
      return this.startCounter;
   }

   public byte[] getNonce() {
      return this.nonce;
   }
}
