package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.util.Arrays;

public class MayoPublicKeyParameters extends MayoKeyParameters {
   private final byte[] p;

   public MayoPublicKeyParameters(MayoParameters var1, byte[] var2) {
      super(false, var1);
      this.p = Arrays.clone(var2);
   }

   public byte[] getP() {
      return Arrays.clone(this.p);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.p);
   }
}
