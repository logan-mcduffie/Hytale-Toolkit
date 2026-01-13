package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.util.Arrays;

public class NTRULPRimePublicKeyParameters extends NTRULPRimeKeyParameters {
   private final byte[] seed;
   private final byte[] roundEncA;

   public NTRULPRimePublicKeyParameters(NTRULPRimeParameters var1, byte[] var2) {
      super(false, var1);
      this.seed = Arrays.copyOfRange(var2, 0, 32);
      this.roundEncA = Arrays.copyOfRange(var2, this.seed.length, var2.length);
   }

   NTRULPRimePublicKeyParameters(NTRULPRimeParameters var1, byte[] var2, byte[] var3) {
      super(false, var1);
      this.seed = Arrays.clone(var2);
      this.roundEncA = Arrays.clone(var3);
   }

   byte[] getSeed() {
      return this.seed;
   }

   byte[] getRoundEncA() {
      return this.roundEncA;
   }

   public byte[] getEncoded() {
      byte[] var1 = new byte[this.getParameters().getPublicKeyBytes()];
      System.arraycopy(this.seed, 0, var1, 0, this.seed.length);
      System.arraycopy(this.roundEncA, 0, var1, this.seed.length, this.roundEncA.length);
      return var1;
   }
}
