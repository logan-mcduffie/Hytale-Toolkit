package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Arrays;

public class HQCPrivateKeyParameters extends HQCKeyParameters {
   private final byte[] sk;

   public HQCPrivateKeyParameters(HQCParameters var1, byte[] var2) {
      super(true, var1);
      this.sk = Arrays.clone(var2);
   }

   public byte[] getPrivateKey() {
      return Arrays.clone(this.sk);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.sk);
   }
}
