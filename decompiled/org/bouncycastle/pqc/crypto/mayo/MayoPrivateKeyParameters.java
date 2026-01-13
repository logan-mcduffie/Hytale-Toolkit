package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.util.Arrays;

public class MayoPrivateKeyParameters extends MayoKeyParameters {
   private final byte[] seed_sk;

   public MayoPrivateKeyParameters(MayoParameters var1, byte[] var2) {
      super(true, var1);
      this.seed_sk = Arrays.clone(var2);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.seed_sk);
   }

   public byte[] getSeedSk() {
      return Arrays.clone(this.seed_sk);
   }
}
