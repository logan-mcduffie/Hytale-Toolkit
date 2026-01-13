package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.util.Arrays;

public class NTRUPublicKeyParameters extends NTRUKeyParameters {
   final byte[] publicKey;

   public NTRUPublicKeyParameters(NTRUParameters var1, byte[] var2) {
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
