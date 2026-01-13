package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.util.Arrays;

public class NTRUPrivateKeyParameters extends NTRUKeyParameters {
   final byte[] privateKey;

   public NTRUPrivateKeyParameters(NTRUParameters var1, byte[] var2) {
      super(true, var1);
      this.privateKey = Arrays.clone(var2);
   }

   public byte[] getPrivateKey() {
      return Arrays.clone(this.privateKey);
   }

   public byte[] getEncoded() {
      return this.getPrivateKey();
   }
}
