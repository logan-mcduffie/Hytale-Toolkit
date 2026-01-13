package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Arrays;

public class HQCPublicKeyParameters extends HQCKeyParameters {
   private final byte[] pk;

   public HQCPublicKeyParameters(HQCParameters var1, byte[] var2) {
      super(true, var1);
      this.pk = Arrays.clone(var2);
   }

   public byte[] getPublicKey() {
      return Arrays.clone(this.pk);
   }

   public byte[] getEncoded() {
      return this.getPublicKey();
   }
}
