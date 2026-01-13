package org.bouncycastle.crypto.params;

public class RC2Parameters extends KeyParameter {
   private int bits;

   public RC2Parameters(byte[] var1) {
      this(var1, var1.length > 128 ? 1024 : var1.length * 8);
   }

   public RC2Parameters(byte[] var1, int var2) {
      super(var1);
      this.bits = var2;
   }

   public int getEffectiveKeyBits() {
      return this.bits;
   }
}
