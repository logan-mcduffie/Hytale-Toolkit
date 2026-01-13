package org.bouncycastle.crypto.modes.kgcm;

public class BasicKGCMMultiplier_512 implements KGCMMultiplier {
   private final long[] H = new long[8];

   @Override
   public void init(long[] var1) {
      KGCMUtil_512.copy(var1, this.H);
   }

   @Override
   public void multiplyH(long[] var1) {
      KGCMUtil_512.multiply(var1, this.H, var1);
   }
}
