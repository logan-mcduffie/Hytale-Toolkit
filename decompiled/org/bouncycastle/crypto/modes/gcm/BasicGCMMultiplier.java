package org.bouncycastle.crypto.modes.gcm;

public class BasicGCMMultiplier implements GCMMultiplier {
   private long[] H;

   @Override
   public void init(byte[] var1) {
      this.H = GCMUtil.asLongs(var1);
   }

   @Override
   public void multiplyH(byte[] var1) {
      GCMUtil.multiply(var1, this.H);
   }
}
