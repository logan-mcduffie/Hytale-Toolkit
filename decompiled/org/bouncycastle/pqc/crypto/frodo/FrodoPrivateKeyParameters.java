package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.util.Arrays;

public class FrodoPrivateKeyParameters extends FrodoKeyParameters {
   private byte[] privateKey;

   public byte[] getPrivateKey() {
      return Arrays.clone(this.privateKey);
   }

   public FrodoPrivateKeyParameters(FrodoParameters var1, byte[] var2) {
      super(true, var1);
      this.privateKey = Arrays.clone(var2);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.privateKey);
   }
}
