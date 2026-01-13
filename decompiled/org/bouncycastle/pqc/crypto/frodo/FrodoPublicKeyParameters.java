package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.util.Arrays;

public class FrodoPublicKeyParameters extends FrodoKeyParameters {
   public byte[] publicKey;

   public byte[] getPublicKey() {
      return Arrays.clone(this.publicKey);
   }

   public byte[] getEncoded() {
      return this.getPublicKey();
   }

   public FrodoPublicKeyParameters(FrodoParameters var1, byte[] var2) {
      super(false, var1);
      this.publicKey = Arrays.clone(var2);
   }
}
