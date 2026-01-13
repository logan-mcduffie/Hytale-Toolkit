package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.util.Arrays;

public class SNTRUPrimePublicKeyParameters extends SNTRUPrimeKeyParameters {
   private final byte[] encH;

   public SNTRUPrimePublicKeyParameters(SNTRUPrimeParameters var1, byte[] var2) {
      super(false, var1);
      this.encH = Arrays.clone(var2);
   }

   byte[] getEncH() {
      return this.encH;
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.encH);
   }
}
