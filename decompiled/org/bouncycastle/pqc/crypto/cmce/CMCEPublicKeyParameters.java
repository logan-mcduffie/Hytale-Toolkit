package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.util.Arrays;

public class CMCEPublicKeyParameters extends CMCEKeyParameters {
   private final byte[] publicKey;

   public byte[] getPublicKey() {
      return Arrays.clone(this.publicKey);
   }

   public byte[] getEncoded() {
      return this.getPublicKey();
   }

   public CMCEPublicKeyParameters(CMCEParameters var1, byte[] var2) {
      super(false, var1);
      this.publicKey = Arrays.clone(var2);
   }
}
