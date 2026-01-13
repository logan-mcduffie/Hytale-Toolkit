package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

class LMOtsPrivateKey {
   private final LMOtsParameters parameter;
   private final byte[] I;
   private final int q;
   private final byte[] masterSecret;

   LMOtsPrivateKey(LMOtsParameters var1, byte[] var2, int var3, byte[] var4) {
      this.parameter = var1;
      this.I = var2;
      this.q = var3;
      this.masterSecret = var4;
   }

   LMSContext getSignatureContext(LMSigParameters var1, byte[][] var2) {
      byte[] var3 = new byte[this.parameter.getN()];
      SeedDerive var4 = this.getDerivationFunction();
      var4.setJ(-3);
      var4.deriveSeed(var3, false);
      Digest var5 = DigestUtil.getDigest(this.parameter);
      LmsUtils.byteArray(this.getI(), var5);
      LmsUtils.u32str(this.getQ(), var5);
      LmsUtils.u16str((short)-32383, var5);
      LmsUtils.byteArray(var3, var5);
      return new LMSContext(this, var1, var5, var3, var2);
   }

   SeedDerive getDerivationFunction() {
      SeedDerive var1 = new SeedDerive(this.I, this.masterSecret, DigestUtil.getDigest(this.parameter));
      var1.setQ(this.q);
      return var1;
   }

   public LMOtsParameters getParameter() {
      return this.parameter;
   }

   public byte[] getI() {
      return this.I;
   }

   public int getQ() {
      return this.q;
   }

   public byte[] getMasterSecret() {
      return this.masterSecret;
   }
}
