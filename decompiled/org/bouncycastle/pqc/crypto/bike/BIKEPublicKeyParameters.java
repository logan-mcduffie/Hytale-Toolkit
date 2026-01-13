package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.util.Arrays;

public class BIKEPublicKeyParameters extends BIKEKeyParameters {
   byte[] publicKey;

   public BIKEPublicKeyParameters(BIKEParameters var1, byte[] var2) {
      super(false, var1);
      this.publicKey = Arrays.clone(var2);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.publicKey);
   }
}
