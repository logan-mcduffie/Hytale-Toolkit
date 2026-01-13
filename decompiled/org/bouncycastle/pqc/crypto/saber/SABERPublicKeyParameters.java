package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.util.Arrays;

public class SABERPublicKeyParameters extends SABERKeyParameters {
   private final byte[] publicKey;

   public SABERPublicKeyParameters(SABERParameters var1, byte[] var2) {
      super(false, var1);
      this.publicKey = Arrays.clone(var2);
   }

   public byte[] getPublicKey() {
      return Arrays.clone(this.publicKey);
   }

   public byte[] getEncoded() {
      return this.getPublicKey();
   }
}
