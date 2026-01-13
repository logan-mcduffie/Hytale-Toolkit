package org.bouncycastle.crypto.modes.kgcm;

public class Tables8kKGCMMultiplier_256 implements KGCMMultiplier {
   private long[][] T;

   @Override
   public void init(long[] var1) {
      if (this.T == null) {
         this.T = new long[256][4];
      } else if (KGCMUtil_256.equal(var1, this.T[1])) {
         return;
      }

      KGCMUtil_256.copy(var1, this.T[1]);

      for (byte var2 = 2; var2 < 256; var2 += 2) {
         KGCMUtil_256.multiplyX(this.T[var2 >> 1], this.T[var2]);
         KGCMUtil_256.add(this.T[var2], this.T[1], this.T[var2 + 1]);
      }
   }

   @Override
   public void multiplyH(long[] var1) {
      long[] var2 = new long[4];
      KGCMUtil_256.copy(this.T[(int)(var1[3] >>> 56) & 0xFF], var2);

      for (int var3 = 30; var3 >= 0; var3--) {
         KGCMUtil_256.multiplyX8(var2, var2);
         KGCMUtil_256.add(this.T[(int)(var1[var3 >>> 3] >>> ((var3 & 7) << 3)) & 0xFF], var2, var2);
      }

      KGCMUtil_256.copy(var2, var1);
   }
}
