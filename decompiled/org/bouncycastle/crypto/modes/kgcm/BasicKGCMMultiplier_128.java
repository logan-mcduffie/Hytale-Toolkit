package org.bouncycastle.crypto.modes.kgcm;

public class BasicKGCMMultiplier_128 implements KGCMMultiplier {
   private final long[] H = new long[2];

   @Override
   public void init(long[] var1) {
      KGCMUtil_128.copy(var1, this.H);
   }

   @Override
   public void multiplyH(long[] var1) {
      KGCMUtil_128.multiply(var1, this.H, var1);
   }
}
