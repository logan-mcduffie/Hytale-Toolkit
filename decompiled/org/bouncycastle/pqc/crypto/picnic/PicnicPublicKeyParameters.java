package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.util.Arrays;

public class PicnicPublicKeyParameters extends PicnicKeyParameters {
   private final byte[] publicKey;

   public PicnicPublicKeyParameters(PicnicParameters var1, byte[] var2) {
      super(false, var1);
      this.publicKey = Arrays.clone(var2);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.publicKey);
   }
}
