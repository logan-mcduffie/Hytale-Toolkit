package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.util.Arrays;

public class SABERPrivateKeyParameters extends SABERKeyParameters {
   private byte[] privateKey;

   public SABERPrivateKeyParameters(SABERParameters var1, byte[] var2) {
      super(true, var1);
      this.privateKey = Arrays.clone(var2);
   }

   public byte[] getPrivateKey() {
      return Arrays.clone(this.privateKey);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.privateKey);
   }
}
