package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.util.Arrays;

public class BIKEPrivateKeyParameters extends BIKEKeyParameters {
   private byte[] h0;
   private byte[] h1;
   private byte[] sigma;

   public BIKEPrivateKeyParameters(BIKEParameters var1, byte[] var2, byte[] var3, byte[] var4) {
      super(true, var1);
      this.h0 = Arrays.clone(var2);
      this.h1 = Arrays.clone(var3);
      this.sigma = Arrays.clone(var4);
   }

   byte[] getH0() {
      return this.h0;
   }

   byte[] getH1() {
      return this.h1;
   }

   byte[] getSigma() {
      return this.sigma;
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(this.h0, this.h1, this.sigma);
   }
}
